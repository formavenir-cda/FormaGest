package com.eni.formagest.bll.users;

import com.eni.formagest.bo.users.UserRole;
import com.eni.formagest.dto.users.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll(UserRole role);

    UserDto create(UserDto dto);

    UserDto update(Long id, UserDto dto);

    UserDto changeRole(Long id, UserDto dto);

    void delete(Long id);

    UserDto toggleActive(Long id);
}
