package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.payloads.GetAllUsersPaginationResponse;
import com.weighbridge.admin.payloads.UpdateRequest;
import com.weighbridge.admin.payloads.UserRequest;
import com.weighbridge.admin.payloads.UserResponse;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.admin.services.UserMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMasterServiceImpl implements UserMasterService {


    private final ModelMapper modelMapper;
    private final UserMasterRepository userMasterRepository;
    private final CompanyMasterRepository companyMasterRepository;
    private final SiteMasterRepository siteMasterRepository;
    private final RoleMasterRepository roleMasterRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final UserHistoryRepository userHistoryRepository;

    @Autowired
   private HttpServletRequest request;


    @Autowired
    private EmailService emailService;


    @Override
    public String createUser(UserRequest userRequest,String userId) {

        // Check if email or contact number already exists
        if (userMasterRepository.existsByUserEmailId(userRequest.getEmailId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Id is already taken");
        }

        // Fetch company and site details if provided
        CompanyMaster companyMaster = companyMasterRepository.findByCompanyName(userRequest.getCompany());
        if (companyMaster == null) {
            throw new ResourceNotFoundException("CompanyMaster", "companyName", userRequest.getCompany());
        }
        String[] siteInfoParts = userRequest.getSite().split(",", 2);
        String siteName=null;
        String siteAddress="";
        if (siteInfoParts.length != 2) {
            //throw new IllegalArgumentException("Invalid format for site info: " + userRequest.getSite());
         siteName = siteInfoParts[0].trim();
        }
        else {
           siteName = siteInfoParts[0].trim();
           siteAddress = siteInfoParts[1].trim();
        }
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddressAndCompanyCompanyId(siteName, siteAddress,companyMaster.getCompanyId());
        if (siteMaster == null) {
            throw new ResourceNotFoundException("SiteMaster", "siteName", siteName);
        }
        // Create UserMaster instance and set properties
        UserMaster userMaster = new UserMaster();
        String userId1 = generateUserId(companyMaster.getCompanyId());
        userMaster.setUserId(userId1);
        userMaster.setCompany(companyMaster);
        userMaster.setSite(siteMaster);
        userMaster.setUserEmailId(userRequest.getEmailId());
        userMaster.setUserContactNo(userRequest.getContactNo());
        userMaster.setUserFirstName(userRequest.getFirstName());
        userMaster.setUserMiddleName(userRequest.getMiddleName());
        userMaster.setUserLastName(userRequest.getLastName());

        LocalDateTime currentDateTime = LocalDateTime.now();
     //   String createdBy = session.getAttribute("userId").toString(); // Assuming the user creation is done by the current session user
        userMaster.setUserCreatedBy(userId);
        userMaster.setUserCreatedDate(currentDateTime);
        userMaster.setUserModifiedBy(userId);
        userMaster.setUserModifiedDate(currentDateTime);

        // Create UserAuthentication instance and set properties
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(userId1);
        Set<String> setOfRoles = userRequest.getRole();
        Set<RoleMaster> roles = new HashSet<>();
        if (setOfRoles != null) {
            setOfRoles.forEach(roleName -> {
                RoleMaster roleMaster = roleMasterRepository.findByRoleName(roleName);
                if (roleMaster != null) {
                    roles.add(roleMaster);
                } else {
                    // Handle case where role doesn't exist
                    throw new ResourceNotFoundException("Role", "roleName", roleName);
                }
            });
        }
        String defaultPassword = generateRandomPassword();
        userAuthentication.setRoles(roles);
        String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt());
        userAuthentication.setDefaultPassword(hashedPassword);

        // Convert Set<String> to comma-separated String
        String rolesString = String.join(",", getRoleNames(roles));

        // Save user and user authentication
        try {
            userMasterRepository.save(userMaster);
            UserAuthentication savedUser = userAuthenticationRepository.save(userAuthentication);
            UserMaster updatedUser = userMasterRepository.save(userMaster);
            UserAuthentication updatedAuthUser = userAuthenticationRepository.save(userAuthentication);

            emailService.sendCredentials(userRequest.getEmailId(), userId1, defaultPassword);
            return "User is created successfully with userId : " + userId1;
        } catch (DataAccessException e) {
            // Catch any database access exceptions and throw an InternalServerError exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
        }
    }

    private String generateRandomPassword() {
        // Generate a random password of length 10
        return RandomStringUtils.randomAlphanumeric(8);
    }


    public synchronized String generateUserId(String companyId) {
        // Count the number of users for the given company ID
        long userCount = userMasterRepository.countByCompanyCompanyId(companyId) + 1;

        // Generate the user ID
        String userId = companyId + "@" + String.format("%02d", userCount);

        return userId;
    }

    @Override
    public GetAllUsersPaginationResponse getAllUsers(Pageable pageable) {
        System.out.println("pageable = " + pageable);
        Page<UserMaster> userPage = userMasterRepository.findAll(pageable);
        System.out.println(userPage);

        Page<UserResponse> responsePage = userPage.map(userMaster -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(userMaster.getUserId());
            userResponse.setFirstName(userMaster.getUserFirstName());
            userResponse.setMiddleName(userMaster.getUserMiddleName());
            userResponse.setLastName(userMaster.getUserLastName());
            userResponse.setEmailId(userMaster.getUserEmailId());
            userResponse.setContactNo(userMaster.getUserContactNo());

            CompanyMaster company = userMaster.getCompany();
            userResponse.setCompany(company != null ? company.getCompanyName() : null);
            SiteMaster site = userMaster.getSite();
            String siteAddress = site.getSiteName() + "," + site.getSiteAddress();
            userResponse.setSite(site != null ? siteAddress : null);

            Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userMaster.getUserId());
            Set<String> roleNames = roleMasters.stream().map(RoleMaster::getRoleName).collect(Collectors.toSet());
            userResponse.setRole(roleNames);
            userResponse.setStatus(userMaster.getUserStatus());

            return userResponse;
        });
        GetAllUsersPaginationResponse getAllUsersPaginationResponse=new GetAllUsersPaginationResponse();
        getAllUsersPaginationResponse.setUsers(responsePage.getContent());
        getAllUsersPaginationResponse.setTotalPages(responsePage.getTotalPages());
        getAllUsersPaginationResponse.setTotalElements(responsePage.getTotalElements());
        return getAllUsersPaginationResponse;
    }

    @Override
    public Page<UserResponse> getAllUsersbyUserStatus(Pageable pageable, String userStatus) {
        System.out.println("pageable = " + pageable);
        // to find by user Status whether it's "ACTIVE" or "INACTIVE"
        Page<UserMaster> userPage = userMasterRepository.findAllByUserStatus(pageable,userStatus);
        System.out.println(userPage);

        Page<UserResponse> responsePage = userPage.map(userMaster -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(userMaster.getUserId());
            userResponse.setFirstName(userMaster.getUserFirstName());
            userResponse.setMiddleName(userMaster.getUserMiddleName());
            userResponse.setLastName(userMaster.getUserLastName());
            userResponse.setEmailId(userMaster.getUserEmailId());
            userResponse.setContactNo(userMaster.getUserContactNo());

            System.out.println("-----------1----------");
            CompanyMaster company = userMaster.getCompany();
            userResponse.setCompany(company != null ? company.getCompanyName() : null);
            System.out.println("-----------2----------");
            SiteMaster site = userMaster.getSite();
            String siteAddress = site.getSiteName() + "," + site.getSiteAddress();
            userResponse.setSite(site != null ? siteAddress : null);
            Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userMaster.getUserId());
            Set<String> roleNames = roleMasters.stream().map(RoleMaster::getRoleName).collect(Collectors.toSet());
            userResponse.setRole(roleNames);
            userResponse.setStatus(userMaster.getUserStatus());
            return userResponse;
        });

        return responsePage;
    }

    @Override
    public UserResponse getSingleUser(String userId) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(userMaster.getUserId());
        userResponse.setFirstName(userMaster.getUserFirstName());
        userResponse.setMiddleName(userMaster.getUserMiddleName());
        userResponse.setLastName(userMaster.getUserLastName());
        userResponse.setEmailId(userMaster.getUserEmailId());
        userResponse.setContactNo(userMaster.getUserContactNo());

        CompanyMaster company = userMaster.getCompany();
        userResponse.setCompany(company.getCompanyName());

        SiteMaster site = userMaster.getSite();
        //combine the sitename with address
        String siteAddress = site.getSiteName() + "," + site.getSiteAddress();

        userResponse.setSite(siteAddress);

        Set<RoleMaster> roleMasters = userAuthenticationRepository.findRolesByUserId(userMaster.getUserId());
        // Convert Set<RoleMaster> to Set<String> using Java Streams
        Set<String> roleNames = roleMasters.stream().map(RoleMaster::getRoleName) // Assuming getRoleName() returns the role name as String
                .collect(Collectors.toSet());
        userResponse.setRole(roleNames);
        userResponse.setStatus(userMaster.getUserStatus());

        return userResponse;
    }

    @Override
    public boolean deleteUserById(String userId,String user) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        LocalDateTime dateTime=LocalDateTime.now();
        if (userMaster.getUserStatus().equals("ACTIVE")) {
            userMaster.setUserStatus("INACTIVE");
            UserHistory userHistory=new UserHistory();
            userHistory.setCompany(userMaster.getCompany().getCompanyName());
            userHistory.setUserModifiedBy(userMaster.getUserModifiedBy());
            userHistory.setUserCreatedBy(userMaster.getUserCreatedBy());
            userHistory.setSite(userMaster.getSite().getSiteId());
            UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);
            String roles = String.join(",", getRoleNames(userAuthentication.getRoles()));
            // Set the roles String to the UseruserHistory
            userHistory.setRoles(roles);
            userHistory.setUserId(userId);
            userHistoryRepository.save(userHistory);
            userMaster.setUserModifiedBy(user);
            userMaster.setUserModifiedDate(dateTime);
            userMasterRepository.save(userMaster);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String updateUserById(UpdateRequest updateRequest, String userId,String user) {
        try {
            // Fetch the existing user from the database
            UserMaster userMaster = userMasterRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

            // Check if the new email or contact number already exists for other users
            boolean userExists = userMasterRepository.existsByUserEmailIdAndUserIdNot(
                    updateRequest.getEmailId(), userId
            );
            if (userExists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "EmailId is exists with another user");
            }

            // Fetch company and site details
            String[] siteInfoParts = updateRequest.getSite().split(",", 2);
            if (siteInfoParts.length != 2){
                throw new IllegalArgumentException("Invalid format for site info: " + updateRequest.getSite());
            }

            String siteName = siteInfoParts[0].trim();
            String siteAddress = siteInfoParts[1].trim();
            SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);
            CompanyMaster companyMaster = companyMasterRepository.findByCompanyName(updateRequest.getCompany());

            // Fetch the user authentication details
            UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);

            // Set user modification details
            String modifiedUser = null;
            LocalDateTime currentDateTime = LocalDateTime.now();

            /*if (session != null && session.getAttribute("userId") != null) {
                modifiedUser = String.valueOf(session.getAttribute("userId"));*/

                // Add update to user history
                UserHistory userHistory = new UserHistory();
                userHistory.setUserId(userId);
                // Convert Set<String> to comma-separated String
                String roles = String.join(",", getRoleNames(userAuthentication.getRoles()));
                // Set the roles String to the UseruserHistory
                userHistory.setRoles(roles);
                System.out.println(roles);

                userHistory.setSite(userMaster.getSite().getSiteName() + ", " +userMaster.getSite().getSiteAddress());
                userHistory.setCompany(userMaster.getCompany().getCompanyName());
                userHistory.setUserCreatedBy(userMaster.getUserCreatedBy());
                userHistory.setUserCreatedDate(userMaster.getUserCreatedDate());
                userHistory.setUserModifiedBy(user);
                userHistory.setUserModifiedDate(userMaster.getUserModifiedDate());

                // Set userMaster object properties from the request
                userMaster.setCompany(companyMaster);
                userMaster.setSite(siteMaster);
                userMaster.setUserEmailId(updateRequest.getEmailId());
                userMaster.setUserContactNo(updateRequest.getContactNo());
                userMaster.setUserFirstName(updateRequest.getFirstName());
                userMaster.setUserMiddleName(updateRequest.getMiddleName());
                userMaster.setUserLastName(updateRequest.getLastName());
                userMaster.setUserModifiedBy(user);
                userMaster.setUserModifiedDate(currentDateTime);

                Set<RoleMaster> updatedRoles = updateRoles(userAuthentication, updateRequest.getRole());

                // Set the updated roles to the userAuthentication object
                userAuthentication.setRoles(updatedRoles);

                // Save updated user and user authentication
                UserMaster updatedUser = userMasterRepository.save(userMaster);
                UserAuthentication updatedAuthUser = userAuthenticationRepository.save(userAuthentication);
                // Save the history
                userHistoryRepository.save(userHistory);

                return "User Updated Succesfully";
           /* } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }*/


        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e; // Re-throwing already handled exceptions
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to Update User", e);
        }

    }

    @Override
    public boolean activateUser(String userId,String user) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        LocalDateTime localDateTime=LocalDateTime.now();
        if (userMaster.getUserStatus().equals("INACTIVE")) {
            UserHistory userHistory=new UserHistory();
            userHistory.setCompany(userMaster.getCompany().getCompanyName());
            userHistory.setUserModifiedBy(userMaster.getUserModifiedBy());
            userHistory.setUserCreatedBy(userMaster.getUserCreatedBy());
            userHistory.setSite(userMaster.getSite().getSiteId());
            UserAuthentication userAuthentication = userAuthenticationRepository.findByUserId(userId);
            String roles = String.join(",", getRoleNames(userAuthentication.getRoles()));
            // Set the roles String to the UseruserHistory
            userHistory.setRoles(roles);
            userHistory.setUserId(userId);
            userHistoryRepository.save(userHistory);
            userMaster.setUserStatus("ACTIVE");
            userMaster.setUserModifiedBy(user);
            userMaster.setUserModifiedDate(localDateTime);
            userMasterRepository.save(userMaster);
            return true;
        }
        return false;
    }


    private Set<RoleMaster> updateRoles(UserAuthentication userAuthentication, Set<String> updatedRoleNames) {
        Set<RoleMaster> updatedRoles = new HashSet<>();
        if (updatedRoleNames != null) {
            Iterable<RoleMaster> roleMasters = roleMasterRepository.findAllByRoleNameIn(updatedRoleNames);
            Map<String, RoleMaster> roleMap = new HashMap<>();
            roleMasters.forEach(role -> roleMap.put(role.getRoleName(), role));
            updatedRoleNames.forEach(roleName -> {
                RoleMaster roleMaster = roleMap.get(roleName);
                if (roleMaster != null) {
                    updatedRoles.add(roleMaster);
                } else {
                    throw new ResourceNotFoundException("Role", "roleName", roleName);
                }
            });
        }
        return updatedRoles;
    }


    private Set<String> getRoleNames(Set<RoleMaster> roles) {
        return roles.stream().map(RoleMaster::getRoleName).collect(Collectors.toSet());
    }

}