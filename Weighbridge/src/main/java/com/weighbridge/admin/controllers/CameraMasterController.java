package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.CameraMasterDto;
import com.weighbridge.admin.services.CameraMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/v1/camera")
public class CameraMasterController {

    @Autowired
    private CameraMasterService cameraMasterService;

    @PostMapping("/cameraMaster")
    public ResponseEntity<String> saveCameraMasterDetail(@RequestBody CameraMasterDto cameraMasterDto, @RequestParam String userId){
        String msg = cameraMasterService.saveCameraUrl(cameraMasterDto, userId);
        return ResponseEntity.ok(msg);
    }
}
