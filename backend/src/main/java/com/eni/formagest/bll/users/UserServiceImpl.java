package com.eni.formagest.bll.users;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.Teacher;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dal.enrollment.EnrollmentRepository;
import com.eni.formagest.dal.training.ScheduledCourseRepository;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SectorRepository sectorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ScheduledCourseRepository scheduledCourseRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, SectorRepository sectorRepository,
                        EnrollmentRepository enrollmentRepository, ScheduledCourseRepository scheduledCourseRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sectorRepository = sectorRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.scheduledCourseRepository = scheduledCourseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(UserRole role) {
        List<User> users = role != null ? userRepository.findByRole(role) : userRepository.findAll();
        return UserMapper.toDtoList(users);
    }

    @Override
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

    @Override
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

    @Override
    @Transactional
    public UserDto changeRole(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow((NoSuchElementException::new));

        if (dto.getRole() != UserRole.ADMINISTRATOR && isLastAdministrator(user)) {
            throw new IllegalStateException(
                    "Impossible de retirer le rôle administrateur au dernier administrateur."
            );
        }

        if (dto.getRole() == user.getRole()) {
            if (user instanceof Teacher teacher) {
                Sector sector = sectorRepository.findById(dto.getSectorId())
                        .orElseThrow((NoSuchElementException::new));
                teacher.setSector(sector);
            }
            if (user instanceof Student student) {
                student.setBirthDate(dto.getBirthDate());
            }
            return UserMapper.toDto(userRepository.save(user));
        }

        if (hasAssociatedData(user)) {
            throw new IllegalStateException(
                    "Impossible de changer le rôle : cet utilisateur a des données associées à son rôle actuel "
                            + "(inscriptions, cours planifiés...)."
            );
        }

        Long sectorId = null;
        if (dto.getRole() == UserRole.TEACHER) {
            sectorId = sectorRepository.findById(dto.getSectorId())
                    .orElseThrow((NoSuchElementException::new))
                    .getId();
        }

        UserRole oldRole = user.getRole();

        user.setEmail(dto.getEmail().strip());
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setRole(dto.getRole());
        userRepository.saveAndFlush(user);

        switch (oldRole) {
            case STUDENT -> userRepository.deleteStudentRow(id);
            case TEACHER -> userRepository.deleteTeacherRow(id);
            case ADMINISTRATIVE_MANAGER -> userRepository.deleteAdministrativeManagerRow(id);
            case ADMINISTRATOR -> userRepository.deleteAdministratorRow(id);
        }

        switch (dto.getRole()) {
            case STUDENT -> userRepository.insertStudentRow(id, dto.getBirthDate());
            case TEACHER -> userRepository.insertTeacherRow(id, sectorId);
            case ADMINISTRATIVE_MANAGER -> userRepository.insertAdministrativeManagerRow(id);
            case ADMINISTRATOR -> userRepository.insertAdministratorRow(id);
        }

        User updated = userRepository.findById(id).orElseThrow(NoSuchElementException::new);
        return UserMapper.toDto(updated);
    }

    private boolean hasAssociatedData(User user) {
        return switch (user.getRole()) {
            case STUDENT -> enrollmentRepository.existsByStudentId(user.getId());
            case ADMINISTRATIVE_MANAGER -> enrollmentRepository.existsByCreatedById(user.getId())
                    || enrollmentRepository.existsByCancelledById(user.getId());
            case TEACHER -> scheduledCourseRepository.existsByTeacherId(user.getId());
            case ADMINISTRATOR -> false;
        };
    }

    @Override
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

    @Override
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
