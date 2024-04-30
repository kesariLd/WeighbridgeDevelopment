package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.payloads.UpdateRequest;
import com.weighbridge.admin.payloads.UserRequest;
import com.weighbridge.admin.payloads.UserResponse;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.admin.services.UserMasterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.*;
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
    private final SequenceGeneratorRepository sequenceGeneratorRepository;
    private final UserHistoryRepository userHistoryRepository;

    @Autowired
    HttpServletRequest request;


    @Autowired
    EmailService emailService;


    @Override
    public String createUser(UserRequest userRequest, HttpSession session) {
        String defaultPassword = generateRandomPassword();


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
        if (siteInfoParts.length != 2) {
            throw new IllegalArgumentException("Invalid format for site info: " + userRequest.getSite());
        }
        String siteName = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts[1].trim();
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);
        if (siteMaster == null) {
            throw new ResourceNotFoundException("SiteMaster", "siteName", siteName);
        }

        // Create UserMaster instance and set properties
        UserMaster userMaster = new UserMaster();
        String userId = generateUserId(companyMaster.getCompanyId(), siteMaster.getSiteId());
        userMaster.setUserId(userId);
        userMaster.setCompany(companyMaster);
        userMaster.setSite(siteMaster);
        userMaster.setUserEmailId(userRequest.getEmailId());
        userMaster.setUserContactNo(userRequest.getContactNo());
        userMaster.setUserFirstName(userRequest.getFirstName());
        userMaster.setUserMiddleName(userRequest.getMiddleName());
        userMaster.setUserLastName(userRequest.getLastName());

        LocalDateTime currentDateTime = LocalDateTime.now();
        String createdBy = session.getAttribute("userId").toString(); // Assuming the user creation is done by the current session user
        userMaster.setUserCreatedBy(createdBy);
        userMaster.setUserCreatedDate(currentDateTime);
        userMaster.setUserModifiedBy(createdBy);
        userMaster.setUserModifiedDate(currentDateTime);


        // Create UserAuthentication instance and set properties
        UserAuthentication userAuthentication = new UserAuthentication();
        userAuthentication.setUserId(userId);
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

            emailService.sendCredentials(userRequest.getEmailId(), userId, defaultPassword);
            return "User is created successfully with userId : " + userId;
        } catch (DataAccessException e) {
            // Catch any database access exceptions and throw an InternalServerError exception
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error occurred", e);
        }
    }

    private String generateRandomPassword() {
        // Generate a random password of length 10
        return RandomStringUtils.randomAlphanumeric(8);
    }


    public synchronized String generateUserId(String companyId, String siteId) {
        // Retrieve the current value of the unique identifier from the database for the given company and site

        SequenceGenerator sequenceGenerator = sequenceGeneratorRepository.findByCompanyIdAndSiteId(companyId, siteId).orElse(new SequenceGenerator(companyId, siteId, 1)); // Initialize to 1 if not found
        int uniqueIdentifier = sequenceGenerator.getNextValue();

        // Concatenate company ID, site ID, and unique identifier
        String userId = companyId + "_" + siteId + "_" + String.format("%02d", uniqueIdentifier);

        // Increment the unique identifier
        uniqueIdentifier = (uniqueIdentifier + 1) % 1000; // Ensure it's always 3 digits

        // Update the unique identifier value in the database
        sequenceGenerator.setNextValue(uniqueIdentifier);
        sequenceGeneratorRepository.save(sequenceGenerator);

        return userId;

    }


//    public static synchronized String generateUserId(String companyId, String siteId) {
//        // Concatenate company ID, site ID, and unique identifier
//
//        String userId = companyId + "_" + siteId + "_" + String.format("%02d", uniqueIdentifier);
//
//        // Increment the unique identifier
//        uniqueIdentifier = (uniqueIdentifier + 1) % 1000; // Ensure it's always 3 digits
//
//        return userId;
//    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
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
    public String deleteUserById(String userId) {
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        if (userMaster.getUserStatus().equals("ACTIVE")) {
            userMaster.setUserStatus("INACTIVE");
            userMasterRepository.save(userMaster);
        }
        return "User is InActive";
    }

    @Override
    public UserResponse updateUserById(UpdateRequest updateRequest, String userId, HttpSession session) {
        try {
            // Fetch the existing user from the database
            UserMaster userMaster = userMasterRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

            // Check if the new email or contact number already exists for other users
            boolean userExists = userMasterRepository.existsByUserEmailIdAndUserIdNotOrUserContactNoAndUserIdNot(
                    updateRequest.getEmailId(), userId, updateRequest.getContactNo(), userId
            );
            if (userExists) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "EmailId and ContactNo is exists with another user");
            }

            // Fetch company and site details
            String[] siteInfoParts = updateRequest.getSite().split(",", 2);
            if (siteInfoParts.length != 2) {
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

            if (session != null && session.getAttribute("userId") != null) {
                modifiedUser = String.valueOf(session.getAttribute("userId"));

                // Add update to user history
                UserHistory userHistory = new UserHistory();
                userHistory.setUserId(userId);
                // Convert Set<String> to comma-separated String
                String roles = String.join(",", getRoleNames(userAuthentication.getRoles()));
                // Set the roles String to the UseruserHistory
                userHistory.setRoles(roles);
                System.out.println(roles);

                userHistory.setSite(siteName + ", " + siteAddress);
                userHistory.setCompany(userMaster.getCompany().getCompanyName());
                userHistory.setUserCreatedBy(userMaster.getUserCreatedBy());
                userHistory.setUserCreatedDate(userMaster.getUserCreatedDate());
                userHistory.setUserModifiedBy(modifiedUser);
                userHistory.setUserModifiedDate(userMaster.getUserModifiedDate());

                // Set userMaster object properties from the request
                userMaster.setCompany(companyMaster);
                userMaster.setSite(siteMaster);
                userMaster.setUserEmailId(updateRequest.getEmailId());
                userMaster.setUserContactNo(updateRequest.getContactNo());
                userMaster.setUserFirstName(updateRequest.getFirstName());
                userMaster.setUserMiddleName(updateRequest.getMiddleName());
                userMaster.setUserLastName(updateRequest.getLastName());
                userMaster.setUserModifiedBy(modifiedUser);
                userMaster.setUserModifiedDate(currentDateTime);
                userMaster.setUserStatus(updateRequest.getUserStatus());

                Set<RoleMaster> updatedRoles = updateRoles(userAuthentication, updateRequest.getRole());

                // Set the updated roles to the userAuthentication object
                userAuthentication.setRoles(updatedRoles);

                // Save updated user and user authentication
                UserMaster updatedUser = userMasterRepository.save(userMaster);
                UserAuthentication updatedAuthUser = userAuthenticationRepository.save(userAuthentication);
                // Save the history
                userHistoryRepository.save(userHistory);


                // Prepare the response object
                UserResponse userResponse = new UserResponse();
                userResponse.setUserId(updatedUser.getUserId());
                userResponse.setFirstName(updatedUser.getUserFirstName());
                userResponse.setMiddleName(updatedUser.getUserMiddleName());
                userResponse.setLastName(updatedUser.getUserLastName());
                userResponse.setEmailId(updatedUser.getUserEmailId());
                userResponse.setContactNo(updatedUser.getUserContactNo());
                userResponse.setCompany(updatedUser.getCompany().getCompanyName());
                userResponse.setSite(updatedUser.getSite().getSiteName());
                userResponse.setRole(getRoleNames(updatedAuthUser.getRoles()));
                //            userResponse.setStatus(updatedUser.getUserStatus());

                return userResponse;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }


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