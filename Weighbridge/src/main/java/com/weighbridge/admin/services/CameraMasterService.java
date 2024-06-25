package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.CameraMasterDto;

import com.weighbridge.admin.payloads.CameraMasterResponse;

import java.util.List;

public interface CameraMasterService {
    String saveCameraUrl(CameraMasterDto cameraMasterDto,String userId);

    List<CameraMasterResponse> getCameraDetails();

    CameraMasterResponse getCameraDetail(Long id);

    public String updateCameraDetails(CameraMasterResponse cameraMasterDto,Long id,String userId);

    String deleteCameraDetails(Long id);

}
