package com.weighbridge.admin.services;

import com.weighbridge.admin.payloads.UpdateRequest;
import com.weighbridge.admin.payloads.UserRequest;
import com.weighbridge.admin.payloads.UserResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserMasterService {

    String createUser(UserRequest userRequest, HttpSession session);

    Page<UserResponse> getAllUsers(Pageable pageable);

    Page<UserResponse> getAllUsersbyUserStatus(Pageable pageable,String userStatus);
    UserResponse getSingleUser(String userId);

    String deleteUserById(String userId);

    UserResponse updateUserById(UpdateRequest updateRequest, String userId,HttpSession session);



}