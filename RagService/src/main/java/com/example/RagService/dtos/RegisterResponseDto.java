package com.example.RagService.dtos;


import com.example.RagService.enums.AccountStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class RegisterResponseDto {

    private int id;
    private String username;
    private String email;
    private Set<String> roles;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt ;
}
