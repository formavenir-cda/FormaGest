package com.eni.formagest.mappers;

import com.eni.formagest.bo.users.Student;
import com.eni.formagest.bo.users.Teacher;
import com.eni.formagest.bo.users.User;
import com.eni.formagest.dto.users.UserDto;

import java.util.List;
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
}
