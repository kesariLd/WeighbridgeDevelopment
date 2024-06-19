package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.RoleMasterDto;
import com.weighbridge.admin.services.RoleMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * This class acts as a REST API controller for managing material master data.
 * It facilitates CRUD (Create, Read, Update, Delete) operations on material entities
 * by exposing well-defined API endpoints.
 * "/api/v1/roles" - This annotation maps all methods of this controller
 * to the base URI "/api/v1/roles".
 */
@RestController
@RequestMapping("/api/v1/roles")
public class RoleMasterController {

    private final RoleMasterService roleMasterService;
    /**
     * Constructs a new RoleMasterController with the provided RoleMasterService.
     * @param roleMasterService The service for handling role-related operations.
     */
    public RoleMasterController(RoleMasterService roleMasterService) {
        this.roleMasterService = roleMasterService;
    }
    /**
     * Endpoint for creating and saving a new role.
     * @param roleDto The request body containing role information to be saved.
     * @return ResponseEntity containing the saved role information and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<RoleMasterDto> createRole(@Validated @RequestBody RoleMasterDto roleDto,String userId) {
        RoleMasterDto roleMasterDto = roleMasterService.createRole(roleDto,userId);
        return new ResponseEntity<>(roleMasterDto, HttpStatus.CREATED);
    }


    /**
     * Endpoint for retrieving all saved roles.
     * @return ResponseEntity containing a list of all saved roles and HTTP status OK.
     */
    @GetMapping()
    public ResponseEntity<List<RoleMasterDto>> getAllRoles() {
        List<RoleMasterDto> roleMasterList = roleMasterService.getAllRole();
        return new ResponseEntity<>(roleMasterList,HttpStatus.OK);
    }
    /**
     * Endpoint for retrieving all role names.
     * @return ResponseEntity containing a list of all role names and HTTP status OK.
     * @deprecated This API should be changed to provide role objects instead of just names.
     */
    @GetMapping("/get/all/role")
    public ResponseEntity<List<String>> getAllRoleNames(){
        List<String> roleMasterList = roleMasterService.getAllRoleNames();
        return new ResponseEntity<>(roleMasterList,HttpStatus.OK);
    }
    /**
     * Endpoint for deleting a role by its name.
     * @param roleName The name of the role to be deleted.
     * @return ResponseEntity with HTTP status NO_CONTENT if the role is deleted successfully,
     *         or HTTP status NOT_FOUND if the role does not exist.
     */
    @DeleteMapping("/{roleName}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleName") String roleName) {
        boolean deleted = roleMasterService.deleteRole(roleName);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
