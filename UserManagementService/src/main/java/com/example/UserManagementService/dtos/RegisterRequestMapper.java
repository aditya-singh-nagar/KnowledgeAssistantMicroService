package com.example.UserManagementService.dtos;


import com.example.UserManagementService.entities.Role;
import com.example.UserManagementService.entities.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RegisterRequestMapper {

    public User registerRequestMapper(RegisterRequestDto dto) {

        User myUser = new User();
        myUser.setUsername(dto.getUsername());
        myUser.setPassword(dto.getPassword());
        myUser.setEmail(dto.getEmail());
       // myUser.setRole(dto.getRole());

        return myUser;

    }

    public RegisterResponseDto registerResponseMapper(User user) {

        RegisterResponseDto registerResponseDto = new RegisterResponseDto();
        registerResponseDto.setId(user.getId());
        registerResponseDto.setUsername(user.getUsername());
        registerResponseDto.setEmail(user.getEmail());
        registerResponseDto.setAccountStatus(user.getAccountStatus());
        registerResponseDto.setCreatedAt(user.getCreatedAt());


        if (user.getRoles() != null) {
            registerResponseDto.setRoles(
                user.getRoles().stream()
                    .map(Role::getRole)
                    .collect(Collectors.toSet())
            );
        }

        return   registerResponseDto;
    }

}
