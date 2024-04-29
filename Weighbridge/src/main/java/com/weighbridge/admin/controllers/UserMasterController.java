package com.weighbridge.admin.controllers;

import com.weighbridge.admin.payloads.UpdateRequest;
import com.weighbridge.admin.payloads.UserRequest;
import com.weighbridge.admin.payloads.UserResponse;
import com.weighbridge.admin.services.UserMasterService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserMasterController {

    private final UserMasterService userMasterService;

    // Create new user
    @PostMapping
    public ResponseEntity<String> createUser(@Validated @RequestBody UserRequest userRequest, HttpSession httpSession){
        String response = userMasterService.createUser(userRequest,httpSession);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false, defaultValue = "userModifiedDate") String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortOrder) {

        Pageable pageable;

        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortField);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<UserResponse> userPage = userMasterService.getAllUsers(pageable);

        List<UserResponse> userLists = userPage.getContent();
        return ResponseEntity.ok(userLists);
    }


//     Get single user by userId
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getSingleUser(@PathVariable("userId") String userId){
        UserResponse user = userMasterService.getSingleUser(userId);
        return ResponseEntity.ok(user);
    }

    // Delete user by userId
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable String userId){
        String response = userMasterService.deleteUserById(userId);
        return ResponseEntity.ok(response);
    }

    // Update user by userId
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<UserResponse> updateUserById(@Validated @RequestBody UpdateRequest updateRequest, @PathVariable String userId,HttpSession httpSession){

        UserResponse response = userMasterService.updateUserById(updateRequest, userId,httpSession);
        return ResponseEntity.ok(response);
    }

    /**
     * http://localhost:8080/api/v1/users/userStatus?userStatus=INACTIVE --> API is like this
     * Param userStatus here added to get all the user by userStatus
     * @param page
     * @param size
     * @param sortField
     * @param sortOrder
     * @param userStatus
     * @return
     */
    @GetMapping("/userStatus")
    public ResponseEntity<List<UserResponse>> getAllUsersByUserStatus(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(required = false, defaultValue = "userModifiedDate") String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortOrder,
            @RequestParam(required = false) String userStatus)  {

        Pageable pageable;

        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortField);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<UserResponse> userPage = userMasterService.getAllUsersbyUserStatus(pageable,userStatus);

        List<UserResponse> userLists = userPage.getContent();
        return ResponseEntity.ok(userLists);
    }



}
