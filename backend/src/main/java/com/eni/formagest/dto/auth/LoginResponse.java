package com.eni.formagest.dto.auth;

import com.eni.formagest.dto.users.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LoginResponse {
    private UserDto user;
}