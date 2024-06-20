package com.weighbridge.admin.services;

import com.weighbridge.admin.payloads.GetAllUsersPaginationResponse;
import com.weighbridge.admin.payloads.UpdateRequest;
import com.weighbridge.admin.payloads.UserRequest;
import com.weighbridge.admin.payloads.UserResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserMasterService {

    String createUser(UserRequest userRequest,String userId);

    GetAllUsersPaginationResponse getAllUsers(Pageable pageable);

    Page<UserResponse> getAllUsersbyUserStatus(Pageable pageable,String userStatus);
    UserResponse getSingleUser(String userId);

    boolean deleteUserById(String userId,String user);

    String updateUserById(UpdateRequest updateRequest, String userId,String user);


    boolean activateUser(String userId,String user);
}