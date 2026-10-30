package com.example.UserManagementService.repository;


import com.example.UserManagementService.entities.User;
import com.example.UserManagementService.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {


    Optional<User> findByEmail(String email);


    List<User> findByAccountStatus(AccountStatus accountStatus);
}
