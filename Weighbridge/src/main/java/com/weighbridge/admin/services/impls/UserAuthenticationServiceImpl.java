package com.weighbridge.admin.services.impls;

import ch.qos.logback.classic.Logger;
import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.entities.UserAuthentication;
import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.exceptions.UserNotFoundException;
import com.weighbridge.admin.payloads.ResetRequest;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.admin.repsitories.UserAuthenticationRepository;
import com.weighbridge.admin.services.UserAuthenticationService;
import com.weighbridge.admin.dtos.LoginDto;
import com.weighbridge.admin.dtos.ResetPasswordDto;

import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.payloads.LoginResponse;
import com.weighbridge.admin.repsitories.UserMasterRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SiteMasterRepository siteMasterRepository;
    @Autowired
    HttpServletRequest request;

    @Override
    public LoginResponse loginUser(LoginDto dto) {
        // Fetch user authentication details along with roles
        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserIdWithRoles(dto.getUserId());
        if (userAuthentication == null) {
            throw new ResourceNotFoundException("User", "userId", dto.getUserId());
        }

        // Fetch user details along with company and site information
        UserMaster userMaster = userMasterRepository.findByUserIdWithCompanyAndSite(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", dto.getUserId()));

            if (userMaster.getUserStatus().equals("INACTIVE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is inactive");
            }

        if (userAuthentication.getUserPassword()==null && userAuthentication.getDefaultPassword()!=null) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessage("Please reset your password.");
            loginResponse.setUserId(dto.getUserId());
            return loginResponse;
        }

        if (!BCrypt.checkpw(dto.getUserPassword(),userAuthentication.getUserPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId or password");
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("User logged in successfully!");

        // Set roles in the response and session
        Set<String> roles = userAuthentication.getRoles().stream()
                .map(RoleMaster::getRoleName)
                .collect(Collectors.toSet());
        loginResponse.setRoles(roles);
       // session.setAttribute("roles", roles);

        // Set user name in the response
        String userName = userMaster.getUserFirstName();
        if (userMaster.getUserMiddleName() != null) {
            userName += " " + userMaster.getUserMiddleName();
        }
        userName += " " + userMaster.getUserLastName();
        loginResponse.setUserName(userName);
        loginResponse.setUserId(userMaster.getUserId());

        return loginResponse;
    }


    @Override
    public UserAuthentication resetPassword(String userId, ResetPasswordDto resetPasswordDto) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);
        System.out.println("password: "+resetPasswordDto.getPassword());
        String hashedPassword = BCrypt.hashpw(resetPasswordDto.getPassword(), BCrypt.gensalt());
        userAuthentication.setUserPassword(hashedPassword);
        UserAuthentication saveUser = userAuthenticationRepository.save(userAuthentication);
        return saveUser;
    }

    @Override
    public boolean forgotPassword(String emailId) {
        String otp = generateOtp();
        // Save the token in the database
        saveResetToken(emailId, otp);
        // Send the token to the user via email
        return emailService.sendPasswordResetEmail(emailId, otp);
    }

    @Override
    public boolean ResetPassword(ResetRequest resetRequest) {
        // Verify the token provided by the user
        Optional<UserMaster> userMasterOptional = userMasterRepository.findByUserEmailId(resetRequest.getEmailId());

        if (userMasterOptional.isPresent()) {
            UserAuthentication user = userAuthenticationRepository.findByUserId(userMasterOptional.get().getUserId());

            log.info("user:", userMasterOptional.get().getUserId());
            if (user == null) {
                throw new UserNotFoundException("User is not found");
            }

            log.info("UserId :", user.getUserId());
            String savedOtp = user.getOtp();
            if (!savedOtp.equals(resetRequest.getOtp())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is incorrect");
            }
            String hashedPassword = BCrypt.hashpw(resetRequest.getNewPassword(), BCrypt.gensalt());
            user.setUserPassword(hashedPassword);
            user.setOtp(null);

            userAuthenticationRepository.save(user);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        UUID uuid = UUID.randomUUID();
        String otp = uuid.toString().substring(0, 6);
        otp = otp.replaceAll("[^0-9]", "");
        return otp;
    }

    public void saveResetToken(String emailId, String resetToken) {
        Optional<UserMaster> userMasterOptional = userMasterRepository.findByUserEmailId(emailId);
        if (userMasterOptional.isPresent()) {
            UserAuthentication user = userAuthenticationRepository.findByUserId(userMasterOptional.get().getUserId());
            if (user == null) {
                throw new ResourceNotFoundException("user not found");
            }
            user.setOtp(resetToken);
            userAuthenticationRepository.save(user);
        } else {
            throw new ResourceNotFoundException("User with EmailId " + emailId + " not found");
        }
    }
}