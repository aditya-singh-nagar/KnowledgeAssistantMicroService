package com.example.UserManagementService.service;


import com.example.UserManagementService.customException.AppException;
import com.example.UserManagementService.customException.NotFound;
import com.example.UserManagementService.customException.UserAlreadyExist;
import com.example.UserManagementService.dtos.*;
import com.example.UserManagementService.entities.Role;
import com.example.UserManagementService.entities.User;
import com.example.UserManagementService.enums.AccountStatus;
import com.example.UserManagementService.enums.SystemCode;
import com.example.UserManagementService.repository.RoleRepository;
import com.example.UserManagementService.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class UserServices {

    
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final RegisterRequestMapper requestMapper ;


    private final BCryptPasswordEncoder getEncoder ;


    public RegisterResponseDto addUser(RegisterRequestDto dto){


        Optional<User> optUser  = userRepository.findByEmail(dto.getEmail());
        if(optUser.isPresent()){

            throw new UserAlreadyExist(SystemCode.AUTH_USER_ALREADY_EXISTS, "User already exists with email: " + dto.getEmail());
        }

        String rawRole = dto.getRole().trim().toUpperCase();
        final String requestedRole = rawRole.startsWith("ROLE_") ? rawRole : "ROLE_" + rawRole;

        if(requestedRole.equals("ROLE_ADMIN")) {
        	throw new AppException(SystemCode.AUTH_SELF_ADMIN_FORBIDDEN, "Users cannot register themselves as ADMIN");
        }

        Role role = roleRepository.findByRole(requestedRole).orElseThrow(()->new NotFound(SystemCode.AUTH_ROLE_NOT_FOUND, "Role not found in database: " + requestedRole));

        User myUser = requestMapper.registerRequestMapper(dto) ;
        myUser.setPassword(getEncoder.encode(dto.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        myUser.setRoles(roles);

        if("ROLE_PUBLIC".equals(requestedRole)) {
        	myUser.setAccountStatus(AccountStatus.ACTIVE);
        }
        else {
        	myUser.setAccountStatus(AccountStatus.PENDING_APPROVAL);
        }

      User user =   userRepository.save(myUser) ;

        RegisterResponseDto response = requestMapper.registerResponseMapper(user) ;

        return response  ;

    }




    public RegisterResponseDto getUser(LoginDto dto) {

        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new NotFound(SystemCode.AUTH_USER_NOT_FOUND, "User not found with email: " + dto.getEmail()));

        if(!getEncoder.matches(dto.getPassword(),user.getPassword())){
        	throw new AppException(SystemCode.AUTH_INVALID_CREDENTIALS, "Invalid password");
        }

        if(user.getAccountStatus() != AccountStatus.ACTIVE){
        	throw new AppException(SystemCode.AUTH_ACCOUNT_NOT_ACTIVE, "Account is not active. Current status: " + user.getAccountStatus());
        }


         List<String> roleNames = user.getRoles().stream().map(m->m.getRole()).collect(Collectors.toList());

            return requestMapper.registerResponseMapper(user);
        }

        public RegisterResponseDto getUserByUserId(int userId) {

            User user = userRepository.findById(userId).orElseThrow(()-> new NotFound(SystemCode.AUTH_USER_NOT_FOUND, "User not found with id: " + userId));
            return requestMapper.registerResponseMapper(user);
        }




    public String approveUser(int userId,String approverEmail) {

    	User user = userRepository.findById(userId).orElseThrow(()-> new NotFound(SystemCode.AUTH_USER_NOT_FOUND, "User with ID does not exist"));

    	if(user.getAccountStatus()==AccountStatus.ACTIVE) {
    		throw new AppException(SystemCode.SYS_BAD_REQUEST, "User "+user.getEmail()+" is already approved");
    	}
    	if(user.getAccountStatus()==AccountStatus.REJECTED) {
    		throw new AppException(SystemCode.SYS_BAD_REQUEST, "User "+user.getEmail()+" is Rejected and cannot be approved");
    	}
    	User approver = userRepository.findByEmail(approverEmail).orElseThrow(()-> new NotFound(SystemCode.AUTH_USER_NOT_FOUND, "Approver Not Found"));

    	boolean isApproverAdmin = approver.getRoles().stream().anyMatch(r->"ROLE_ADMIN".equals(r.getRole()));
    	boolean isApproverManager = approver.getRoles().stream()
    	            .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));

        boolean targetIsManager = user.getRoles().stream()
                .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
        boolean targetIsEmployee = user.getRoles().stream()
                .anyMatch(r -> "ROLE_EMPLOYEE".equals(r.getRole()));

        if(targetIsManager && !isApproverAdmin) {
        	throw new AppException(SystemCode.AUTH_APPROVAL_FORBIDDEN, "Only an ADMIN can approve a MANAGER account!");
        }

        if (targetIsEmployee && !isApproverManager && !isApproverAdmin) {
            throw new AppException(SystemCode.AUTH_APPROVAL_FORBIDDEN, "Only a MANAGER or ADMIN can approve an EMPLOYEE account!");
        }

        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setApprovedAt(LocalDate.now());
        user.setApprovedBy(approver.getEmail());
        userRepository.save(user);

    	return "User "+user.getEmail()+" has been approved successfully..";
    }




    public List<RegisterResponseDto> getPendingUser(String userEmail) {
    	User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new NotFound("User with the email do not exist"));

    	boolean isAdmin = user.getRoles().stream().anyMatch(r->"ROLE_ADMIN".equals(r.getRole()));
    	boolean isManager = user.getRoles().stream()
    	            .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));

    	List<User> pendingUsers = userRepository.findByAccountStatus(AccountStatus.PENDING_APPROVAL);

    	if(isAdmin) {
    		return pendingUsers.stream().map(requestMapper::registerResponseMapper).collect(Collectors.toList());
    	}
    	else if(isManager) {
    		return pendingUsers.stream().filter(u->u.getRoles().stream().anyMatch(r->"ROLE_EMPLOYEE".equals(r.getRole()))).map(requestMapper::registerResponseMapper)
    				.collect(Collectors.toList());
    	}
    	return List.of();
    }

    public String rejectUser(int userId,String approverEmail){

    	User user = userRepository.findById(userId).orElseThrow(()-> new NotFound(SystemCode.AUTH_USER_NOT_FOUND, "User with the ID does not exist"));

    	if (user.getAccountStatus() == AccountStatus.REJECTED) {
            throw new AppException(SystemCode.SYS_BAD_REQUEST, "User " + user.getEmail() + " is already rejected.");
        }
        if (user.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new AppException(SystemCode.SYS_BAD_REQUEST, "User " + user.getEmail() + " is already approved/active and cannot be rejected.");
        }

    	User approverUser = userRepository.findByEmail(approverEmail).orElseThrow(()-> new NotFound(SystemCode.AUTH_USER_NOT_FOUND, "Approver with the email does not exist"));

    	boolean isApproverAdmin = approverUser.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getRole()));
        boolean isApproverManager = approverUser.getRoles().stream()
                .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
        boolean targetIsManager = user.getRoles().stream()
                .anyMatch(r -> "ROLE_MANAGER".equals(r.getRole()));
        boolean targetIsEmployee = user.getRoles().stream()
                .anyMatch(r -> "ROLE_EMPLOYEE".equals(r.getRole()));

        if(targetIsManager && !isApproverAdmin) {
        	throw new AppException(SystemCode.AUTH_APPROVAL_FORBIDDEN, "Only an ADMIN can reject a MANAGER account!");
        }

        if(targetIsEmployee && !isApproverAdmin && !isApproverManager) {
        	throw new AppException(SystemCode.AUTH_APPROVAL_FORBIDDEN, "Only an ADMIN or MANAGER can reject an EMPLOYEE account!");
        }

        user.setAccountStatus(AccountStatus.REJECTED);
        userRepository.save(user);

    	return "User "+ user.getEmail()+" has been rejected..";
    }


    public List<RegisterResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(requestMapper::registerResponseMapper)
                .collect(Collectors.toList());
    }


    }


