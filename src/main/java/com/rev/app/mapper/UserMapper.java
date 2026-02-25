package com.rev.app.mapper;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setTwoFactorEnabled(user.isTwoFactorEnabled());
        return dto;
    }
}
