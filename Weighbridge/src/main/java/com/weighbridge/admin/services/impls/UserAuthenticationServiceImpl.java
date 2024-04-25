package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.entities.UserAuthentication;
import com.weighbridge.admin.entities.UserMaster;
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
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;

    @Value("${app.default-password}")
    private String defaultPassword;

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

        if (userAuthentication.getUserPassword()==null&&userAuthentication.getDefaultPassword()!=null) {
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setMessage("Please reset your password.");
            loginResponse.setUserId(dto.getUserId());
            return loginResponse;
        }

        if (!BCrypt.checkpw(dto.getUserPassword(),userAuthentication.getUserPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId or password");
        }


        // Set session attributes
        HttpSession session = request.getSession();
        try {
            session.setAttribute("userId", dto.getUserId());
            session.setAttribute("userSite", userMaster.getSite().getSiteId());
            session.setAttribute("userCompany", userMaster.getCompany().getCompanyId());
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Session Expired, Login again");
        }

        // Prepare login response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("User logged in successfully!");

        // Set roles in the response and session
        Set<String> roles = userAuthentication.getRoles().stream()
                .map(RoleMaster::getRoleName)
                .collect(Collectors.toSet());
        loginResponse.setRoles(roles);
        session.setAttribute("roles", roles);

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
}