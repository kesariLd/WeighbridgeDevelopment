package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.CameraMasterDto;

import com.weighbridge.admin.payloads.CameraMasterResponse;

import com.weighbridge.admin.services.CameraMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



import java.util.List;


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


    @GetMapping("/getCameraDetails")
    public ResponseEntity<List<CameraMasterResponse>> getCameraMasterDetails(){
        List<CameraMasterResponse> cameraDetails = cameraMasterService.getCameraDetails();
        return ResponseEntity.ok(cameraDetails);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<CameraMasterResponse> getById(@PathVariable Long id){
        CameraMasterResponse cameraDetail = cameraMasterService.getCameraDetail(id);
        return ResponseEntity.ok(cameraDetail);
    }

    @PutMapping("/updateByCamId/{id}")
    public ResponseEntity<String> updateCameraDetails(@RequestBody CameraMasterResponse cameraMasterDto,@PathVariable Long id,@RequestParam String userId){
        String msg = cameraMasterService.updateCameraDetails(cameraMasterDto, id, userId);
        return ResponseEntity.ok(msg);
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<String> deleteCameraDetails(@PathVariable Long id){
        String msg = cameraMasterService.deleteCameraDetails(id);
        return ResponseEntity.ok(msg);
    }

}
