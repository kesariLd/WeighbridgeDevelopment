package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.CameraMasterDto;

public interface CameraMasterService {
    String saveCameraUrl(CameraMasterDto cameraMasterDto,String userId);
}
