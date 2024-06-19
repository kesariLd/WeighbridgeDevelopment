package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.RoleMasterDto;
import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.exceptions.ResourceCreationException;
import com.weighbridge.admin.repsitories.RoleMasterRepository;
import com.weighbridge.admin.services.RoleMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleMasterServiceImpl implements RoleMasterService {

    private final RoleMasterRepository roleMasterRepository;
    private final ModelMapper modelMapper;
    private final HttpServletRequest request;

    public RoleMasterServiceImpl(RoleMasterRepository roleMasterRepository,
                                 ModelMapper modelMapper,
                                 HttpServletRequest request) {
        this.roleMasterRepository = roleMasterRepository;
        this.modelMapper = modelMapper;
        this.request = request;
    }

    @Override
    public RoleMasterDto createRole(RoleMasterDto roleDto,String userId) {
        try {
            // Check if a role with the given name already exists
            RoleMaster byRoleName = roleMasterRepository.findByRoleName(roleDto.getRoleName());
            if (byRoleName != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
            }
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please Provide userId");
            }
            // Get the current user's session and set creation details
           /* HttpSession session = request.getSession();
            String loggedInUserId;
            if (session != null && session.getAttribute("userId") != null) {
                loggedInUserId = session.getAttribute("userId").toString();
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }*/

            roleDto.setRoleCreatedBy(userId);
            roleDto.setRoleCreatedDate(LocalDateTime.now());

            // Map DTO to entity and save the role
            RoleMaster role = modelMapper.map(roleDto, RoleMaster.class);
            RoleMaster savedRole = roleMasterRepository.save(role);

            // Return the saved role DTO
            return modelMapper.map(savedRole, RoleMasterDto.class);
        } catch (ResponseStatusException e) {
            // If the role already exists, rethrow the exception
            throw e;
        } catch (Exception e) {
            // If any other unexpected error occurs, handle it and provide a generic error message
            throw new ResourceCreationException("Failed to create role", e);
        }
    }

    @Override
    public boolean deleteRole(String roleName) {
        RoleMaster roleMaster = roleMasterRepository.findByRoleName(roleName);
        if (roleMaster != null && roleMaster.getRoleStatus() == "ACTIVE") {
                roleMaster.setRoleStatus("INACTIVE");
                roleMasterRepository.save(roleMaster);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getAllRoleNames() {
        List<String> allRoleNames = roleMasterRepository.findAllRoleListName();
        if (allRoleNames == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No roles names found");
        }
        return allRoleNames;
    }

    @Override
    public List<RoleMasterDto> getAllRole() {
        List<RoleMaster> allRoles = roleMasterRepository.findAll();
        if (allRoles == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No roles found");
        }

        return allRoles.stream()
                .map(roleMaster -> modelMapper.map(roleMaster, RoleMasterDto.class))
                .collect(Collectors.toList());
    }
}