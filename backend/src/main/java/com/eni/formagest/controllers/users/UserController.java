package com.eni.formagest.controllers.users;

import com.eni.formagest.bll.training.SectorService;
import com.eni.formagest.bll.users.UserService;
import com.eni.formagest.controllers.training.SectorController;
import com.eni.formagest.dto.users.UserDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public List<UserDto> findAll() {
        return userService.findAll();
    }
}
