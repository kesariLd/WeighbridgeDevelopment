package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.LoginDto;
import com.weighbridge.admin.dtos.ResetPasswordDto;
import com.weighbridge.admin.entities.UserAuthentication;
import com.weighbridge.admin.payloads.LoginResponse;
import com.weighbridge.admin.payloads.ResetRequest;
import com.weighbridge.admin.services.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for managing user authentication operations. :: Login, Reset Password,
 */
/**
 * Controller class for managing user authentication operations.
 */
@RestController
@RequestMapping("/api/v1/auths")
public class UserAuthenticationController {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    /**
     * Logs in a user.
     * @param loginDto The DTO containing user login credentials.
     * @return ResponseEntity containing the login response and HTTP status OK.
     */
    @PostMapping("/logIn")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDto loginDto){
        LoginResponse response = userAuthenticationService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Resets the password for a user.
     * @param userId The ID of the user whose password is to be reset.
     * @param resetPasswordDto The DTO containing the new password information.
     * @return ResponseEntity with a success message and HTTP status OK.
     */
    @PostMapping("/reset/{userId}")
    public ResponseEntity<String> resetPassword(@PathVariable String userId,@RequestBody ResetPasswordDto resetPasswordDto){
        UserAuthentication userAuthentication=userAuthenticationService.resetPassword(userId,resetPasswordDto);
        return new ResponseEntity<>("Password Reset Succesful!",HttpStatus.OK);
    }

    /**
     * Sends password reset instructions to a user's email.
     * @param emailId The email ID of the user requesting the password reset.
     * @return ResponseEntity with a success or error message and appropriate HTTP status.
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestParam String emailId){
        boolean passwordResetSent = userAuthenticationService.forgotPassword(emailId);
        if (passwordResetSent) {
            return ResponseEntity.ok("Password reset instructions sent successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send password reset instructions.");
        }
    }

    /**
     * Resets the user's password when user click forget password than , to reset in that time using a reset token.
     * @param resetRequest The request containing the reset token and new password.
     * @return ResponseEntity with a success or error message and appropriate HTTP status.
     */
    @PostMapping("/forget/reset-password")
    public ResponseEntity<String> forgetResetPassword(@RequestBody ResetRequest resetRequest) {
        boolean passwordReset = userAuthenticationService.ResetPassword(resetRequest);
        if (passwordReset) {
            return ResponseEntity.ok("Password reset successful.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid reset token or user ID. Password reset failed.");
        }
    }
}


