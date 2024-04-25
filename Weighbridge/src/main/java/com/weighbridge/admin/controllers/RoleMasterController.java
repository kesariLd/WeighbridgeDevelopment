package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.RoleMasterDto;
import com.weighbridge.admin.services.RoleMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleMasterController {

    private final RoleMasterService roleMasterService;



//    @GetMapping("/get/name/{name}")
//    public ResponseEntity<RoleMasterDto> getRoleMasterbyName(@PathVariable String name){
//        RoleMasterDto rolebyName = roleMasterService.getRolebyName(name);
//        return new ResponseEntity<>(rolebyName,HttpStatus.OK);
//    }
//    @GetMapping("/get/id/{id}")
//    public ResponseEntity<RoleMasterDto> getRoleMasterbyId(@PathVariable int id){
//        RoleMasterDto rolebyId = roleMasterService.getRolebyId(id);
//        return new ResponseEntity<>(rolebyId,HttpStatus.OK);
//    }

    // Get all roles as object
    @GetMapping("/get/all")
    public ResponseEntity<List<RoleMasterDto>> getRoleMasterAll(){
        List<RoleMasterDto> roleMasterList = roleMasterService.getAllRole();
        return new ResponseEntity<>(roleMasterList,HttpStatus.OK);
    }

    @GetMapping("/get/all/role")
    public ResponseEntity<List<String>> getRoleMasterListName(){
        List<String> roleMasterList = roleMasterService.getAllStringRole();
        return new ResponseEntity<>(roleMasterList,HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<RoleMasterDto> createRole(@Validated @RequestBody RoleMasterDto roleDto){
        RoleMasterDto roleMasterDto = roleMasterService.createRole(roleDto);
        return new ResponseEntity<>(roleMasterDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String>  deleteRole(@PathVariable("roleId") int roleId){
        roleMasterService.deleteRole(roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }


}
