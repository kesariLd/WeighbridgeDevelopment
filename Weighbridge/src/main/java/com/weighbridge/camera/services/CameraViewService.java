package com.weighbridge.camera.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

public interface CameraViewService {

        String saveCameraPath(String url);
        String getUrlPath(Integer ticketNo,String role);

        String uploadImages(Integer ticketNo, MultipartFile frontImg1, MultipartFile backImg2, MultipartFile topImg3,
                            MultipartFile bottomImg4, MultipartFile leftImg5, MultipartFile rightImg6,
                            String role,String truckStatus) throws IOException;

        Map<String, byte[]> downloadImages(Integer ticketNo, String role, String companyId, String siteId,String truckStatus);


}
