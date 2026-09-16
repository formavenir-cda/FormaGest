package com.eni.formagest.bll.users;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.Teacher;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dal.training.SectorRepository;
import com.eni.formagest.dal.users.UserRepository;
import com.eni.formagest.dto.users.UserDto;
import com.eni.formagest.mappers.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SectorRepository sectorRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SectorRepository sectorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sectorRepository = sectorRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAll(UserRole role) {
        List<User> users = role != null ? userRepository.findByRole(role) : userRepository.findAll();
        return UserMapper.toDtoList(users);
    }

    @Transactional
    public UserDto create(UserDto dto) {
        String email = dto.getEmail().strip();
        UserRole role = dto.getRole();
        Sector sector = null;

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException();
        }

        String password = passwordEncoder.encode(dto.getPassword());

        if (role == UserRole.TEACHER) {
            sector = sectorRepository.findById(dto.getSectorId())
                    .orElseThrow((NoSuchElementException::new));
        }

        User user = UserMapper.toBo(dto, password, sector);
        User userSaved = userRepository.save(user);
        return UserMapper.toDto(userSaved);
    }

    @Transactional
    public UserDto update(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow((NoSuchElementException::new));
        String email = dto.getEmail().strip();
        UserRole role = dto.getRole();
        Sector sector = null;

        if(!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException();
        }

        user.setEmail(email);
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (role == UserRole.TEACHER && user instanceof Teacher teacher) {
            sector = sectorRepository.findById(dto.getSectorId())
                    .orElseThrow((NoSuchElementException::new));
            teacher.setSector(sector);
        }
        if (role == UserRole.STUDENT && user instanceof Student student) {
            student.setBirthDate(dto.getBirthDate());
        }
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto changeRole(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow((NoSuchElementException::new));

        if (dto.getRole() != UserRole.ADMINISTRATOR && isLastAdministrator(user)) {
            throw new IllegalStateException(
                    "Impossible de retirer le rôle administrateur au dernier administrateur."
            );
        }

        Sector sector = null;
        if (dto.getRole() == UserRole.TEACHER) {
                sector = sectorRepository.findById(dto.getSectorId())
                    .orElseThrow((NoSuchElementException::new));
        }
        User newUser = UserMapper.toBo(dto, user.getPassword(), sector);
        userRepository.delete(user);
        userRepository.flush();
        return UserMapper.toDto(userRepository.save(newUser));
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow((NoSuchElementException::new));

        if (isLastAdministrator(user)) {
            throw new IllegalStateException("Impossible de supprimer le dernier administrateur.");
        }

        userRepository.delete(user);
        userRepository.flush();
    }

    @Transactional
    public UserDto toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow((NoSuchElementException::new));

        if (user.isActive() && isLastAdministrator(user)) {
            throw new IllegalStateException("Impossible de désactiver le dernier administrateur.");
        }

        user.setActive(!user.isActive());
        return UserMapper.toDto(userRepository.save(user));
    }

    private boolean isLastAdministrator(User user) {
        return user.getRole() == UserRole.ADMINISTRATOR
                && userRepository.findByRole(UserRole.ADMINISTRATOR).size() <= 1;
    }

}
