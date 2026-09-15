package com.eni.formagest.mappers;

import com.eni.formagest.bo.training.Sector;
import com.eni.formagest.bo.users.*;
import com.eni.formagest.dto.users.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        UserDto.UserDtoBuilder dto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .active(user.isActive())
                .role(user.getRole());

        if (user instanceof Student student) {
            dto.birthDate(student.getBirthDate());
        }

        if (user instanceof Teacher teacher) {
            dto.sectorId(teacher.getSector().getId());
        }

        return dto.build();
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public static User toBo(UserDto dto, String password, Sector sector) {
        return switch (dto.getRole()) {
            case STUDENT -> Student.builder()
                    .email(dto.getEmail())
                    .lastName(dto.getLastName())
                    .firstName(dto.getFirstName())
                    .password(password)
                    .active(dto.isActive())
                    .role(dto.getRole())
                    .birthDate(dto.getBirthDate())
                    .build();
            case TEACHER -> Teacher.builder()
                    .email(dto.getEmail())
                    .lastName(dto.getLastName())
                    .firstName(dto.getFirstName())
                    .password(password)
                    .active(dto.isActive())
                    .role(dto.getRole())
                    .sector(sector)
                    .build();
            case ADMINISTRATIVE_MANAGER -> AdministrativeManager.builder()
                    .email(dto.getEmail())
                    .lastName(dto.getLastName())
                    .firstName(dto.getFirstName())
                    .password(password)
                    .active(dto.isActive())
                    .role(dto.getRole())
                    .build();
            case ADMINISTRATOR -> Administrator.builder()
                    .email(dto.getEmail())
                    .lastName(dto.getLastName())
                    .firstName(dto.getFirstName())
                    .password(password)
                    .active(dto.isActive())
                    .role(dto.getRole())
                    .build();
        };
    }
}
