package com.example.UserManagementService.controller;


import com.example.UserManagementService.dtos.LoginDto;
import com.example.UserManagementService.dtos.RegisterRequestDto;
import com.example.UserManagementService.dtos.RegisterResponseDto;
import com.example.UserManagementService.service.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/add")
    public ResponseEntity<RegisterResponseDto> AddNewUser(@Valid @RequestBody RegisterRequestDto dto){

        return new ResponseEntity<>(userServices.addUser(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<RegisterResponseDto> checkuser(@Valid @RequestBody LoginDto dto){

        return  new ResponseEntity<>(userServices.getUser(dto), HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
    public ResponseEntity<RegisterResponseDto> getUserByUSerId(@PathVariable int userId){

        return  new ResponseEntity<>(userServices.getUserByUserId(userId), HttpStatus.OK);
    }


    @GetMapping("/get/email/{email}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER','PUBLIC')")
    public ResponseEntity<RegisterResponseDto> getUserByEmail( @PathVariable String email){

        return new  ResponseEntity<>(userServices.getUserByEmail(email), HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveUser(@PathVariable int id, Principal principal) {
        // principal.getName() automatically retrieves the email of the person who logged in (from the JWT token)
        String message = userServices.approveUser(id, principal.getName());
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<List<RegisterResponseDto>> getPendinUsers(Principal principal){
    	return ResponseEntity.ok(userServices.getPendingUser(principal.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectUser(@PathVariable int id, Principal principal) {
        String message = userServices.rejectUser(id, principal.getName());
        return ResponseEntity.ok(message);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getall")
    public ResponseEntity<List<RegisterResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userServices.getAllUsers());
    }


}
