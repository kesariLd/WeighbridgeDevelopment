package com.weighbridge.admin.controllers;

import com.weighbridge.admin.services.ParameterMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parameters")
public class ParameterMasterController {
    private final ParameterMasterService parameterMasterService;

    public ParameterMasterController(ParameterMasterService parameterMasterService) {
        this.parameterMasterService = parameterMasterService;
    }

    @PostMapping()
    public ResponseEntity<Long> createParameter(@RequestParam String parameterName) {
        long parameterId = parameterMasterService.createParameter(parameterName);
        return new ResponseEntity<>(parameterId, HttpStatus.CREATED);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllParameterNames() {
        List<String> allNames = parameterMasterService.getAllParameterNames();
        return ResponseEntity.ok(allNames);
    }
}
