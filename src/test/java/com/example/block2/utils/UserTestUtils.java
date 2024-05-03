package com.example.block2.utils;

import com.example.block2.dto.UserDto;
import com.example.block2.entity.Role;

public class UserTestUtils {
    public static void createUserDto(String username, String password, Role role, String email) {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setRole(role);
        userDto.setEmail(email);
    }
}
