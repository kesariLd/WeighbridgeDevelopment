package com.weighbridge.admin.controllers;

import com.weighbridge.admin.payloads.GetAllUsersPaginationResponse;
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
/**
 * REST controller for managing user-related operations.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserMasterController {

    private final UserMasterService userMasterService;

    /**
     * Endpoint for creating a new user.
     * @param userRequest The request body containing user information.
     * @return ResponseEntity containing a success message and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<String> createUser(@Validated @RequestBody UserRequest userRequest,@RequestParam String userId) {
        String response = userMasterService.createUser(userRequest, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint for retrieving all users.
     * @param page The page number for pagination (default: 0).
     * @param size The size of each page for pagination (default: 10).
     * @param sortField The field to sort by (default: userModifiedDate).
     * @param sortOrder The sort order (default: desc).
     * @return ResponseEntity containing a list of users and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<GetAllUsersPaginationResponse> getAllUsers(@RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "10", required = false) int size, @RequestParam(required = false, defaultValue = "userModifiedDate") String sortField, @RequestParam(defaultValue = "desc", required = false) String sortOrder) {

        Pageable pageable;

        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortField);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        GetAllUsersPaginationResponse userLists = userMasterService.getAllUsers(pageable);
        return ResponseEntity.ok(userLists);
    }


    /**
     * Endpoint for retrieving a single user by userId.
     * @param userId The ID of the user to retrieve.
     * @return ResponseEntity containing the requested user and HTTP status OK.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getSingleUser(@PathVariable("userId") String userId) {
        UserResponse user = userMasterService.getSingleUser(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Endpoint for deactivating a user by userId.
     * @param userId The ID of the user to deactivate.
     * @return ResponseEntity with HTTP status NO_CONTENT if user is successfully deactivated, otherwise NOT_FOUND.
     */
    @DeleteMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deleteUserById(@PathVariable String userId,@RequestParam String user) {
        boolean deleted = userMasterService.deleteUserById(userId,user);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for activating a user by userId.
     * @param userId The ID of the user to activate.
     * @return ResponseEntity with HTTP status OK if user is successfully activated, otherwise NOT_FOUND.
     */
    @PutMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable String userId,@RequestParam String user) {
        boolean activated = userMasterService.activateUser(userId,user);
        if (activated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint for updating a user by userId.
     * @param updateRequest The request body containing updated user information.
     * @param userId The ID of the user to update.
     * @return ResponseEntity containing a success message and HTTP status OK.
     */
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<String> updateUserById(@Validated @RequestBody UpdateRequest updateRequest, @PathVariable String userId,@RequestParam String user) {

        String userResponse = userMasterService.updateUserById(updateRequest, userId, user);
        return ResponseEntity.ok(userResponse);
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