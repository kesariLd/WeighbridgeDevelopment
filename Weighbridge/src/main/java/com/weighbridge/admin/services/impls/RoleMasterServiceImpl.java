package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.repsitories.RoleMasterRepository;
import com.weighbridge.admin.services.RoleMasterService;
import com.weighbridge.admin.dtos.RoleMasterDto;
import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.exceptions.ResourceCreationException;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleMasterServiceImpl implements RoleMasterService {

    private final RoleMasterRepository roleMasterRepository;
    private final ModelMapper modelMapper;

    @Autowired
    HttpServletRequest request;
    @Override
    public RoleMasterDto createRole(RoleMasterDto roleDto) {
        try {
            // Check if a role with the given name already exists
            RoleMaster byRoleName = roleMasterRepository.findByRoleName(roleDto.getRoleName());
            if (byRoleName != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists");
            }

            // Get the current user's session and set creation details
            HttpSession session = request.getSession();
            String loggedInUserId;
            if (session != null && session.getAttribute("userId") != null) {
                loggedInUserId = session.getAttribute("userId").toString();
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }

            roleDto.setRoleCreatedBy(loggedInUserId);
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
    public void deleteRole(int roleId) {
        try {
            RoleMaster roleMaster = roleMasterRepository.findById(roleId).get();
            if(roleMaster.getRoleStatus()=="ACTIVE"){
                roleMaster.setRoleStatus("INACTIVE");
                roleMasterRepository.save(roleMaster);

            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Role","roleName", roleMasterRepository.findRoleNameByRoleId(roleId));
        }
    }

    @Override
    public List<String> getAllStringRole() {

        try{
            List<String> allRoleListName = roleMasterRepository.findAllRoleListName();
            return allRoleListName;
        }
        catch (Exception e){
            throw new ResourceNotFoundException("Failed to retrieve roles");
        }

    }

    @Override
    public List<RoleMasterDto> getAllRole() {
        try {
            List<RoleMaster> allRoles = roleMasterRepository.findAll();
            return allRoles.stream()
                    .map(roleMaster -> modelMapper.map(roleMaster, RoleMasterDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Failed to retrieve roles");
        }

    }
}