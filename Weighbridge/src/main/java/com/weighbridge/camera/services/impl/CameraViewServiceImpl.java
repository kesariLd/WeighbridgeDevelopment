package com.weighbridge.camera.services.impl;

import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.RoleMasterRepository;
import com.weighbridge.camera.entites.CameraView;
import com.weighbridge.camera.repositories.CameraRepository;
import com.weighbridge.camera.services.CameraViewService;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CameraViewServiceImpl implements CameraViewService {
    @Value("${nextcloud.base-url}")
    private String baseUrl;

    @Value("${nextcloud.username}")
    private String username;

    @Value("${nextcloud.password}")
    private String password;
    @Autowired
    private CameraRepository cameraRepository;
    @Autowired
    private RoleMasterRepository roleMasterRepository;
    @Override
    public String saveCameraPath(String url) {

        return null;
    }

    @Override
    public String getUrlPath(Integer ticketNo, String role) {
        return null;
    }

    public String uploadImages(Integer ticketNo, MultipartFile frontImg1, MultipartFile backImg2, MultipartFile topImg3,
                               MultipartFile bottomImg4, MultipartFile leftImg5, MultipartFile rightImg6,
                               String role,String truckStatus) throws IOException {
        CameraView cameraView = new CameraView();
        cameraView.setTicketNo(ticketNo);
        cameraView.setTruckStatus(truckStatus);
        cameraView.setDate(LocalDate.now());
        if(role==null){
            throw new ResourceNotFoundException("role is not given");
        }
        Integer roleIdByRoleName = roleMasterRepository.findRoleIdByRoleName(role);
        if(roleIdByRoleName==null){
            throw new ResourceNotFoundException("role is not found "+role);
        }
        cameraView.setRoleId(roleIdByRoleName);

        // Check each MultipartFile before setting its path
        if (frontImg1 != null && !frontImg1.isEmpty()) {
            cameraView.setFrontImg1(uploadAndReturnFilePath(frontImg1));
        }
        if (backImg2 != null && !backImg2.isEmpty()) {
            cameraView.setBackImg2(uploadAndReturnFilePath(backImg2));
        }
        if (topImg3 != null && !topImg3.isEmpty()) {
            cameraView.setTopImg3(uploadAndReturnFilePath(topImg3));
        }
        if (bottomImg4 != null && !bottomImg4.isEmpty()) {
            cameraView.setBottomImg4(uploadAndReturnFilePath(bottomImg4));
        }
        if (leftImg5 != null && !leftImg5.isEmpty()) {
            cameraView.setLeftImg5(uploadAndReturnFilePath(leftImg5));
        }
        if (rightImg6 != null && !rightImg6.isEmpty()) {
            cameraView.setRightImg6(uploadAndReturnFilePath(rightImg6));
        }

        cameraRepository.save(cameraView);
        return "Upload Successfully";
    }



    private String uploadAndReturnFilePath(MultipartFile multipartFile) throws IOException {
        String remoteFilePath = multipartFile.getOriginalFilename();
        String uploadUrl = baseUrl + "/remote.php/dav/files/" + username + "/" + remoteFilePath;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(uploadUrl);

            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpPut.setHeader("Authorization", "Basic " + encodedAuth);

            InputStreamEntity reqEntity = new InputStreamEntity(multipartFile.getInputStream(), multipartFile.getSize());
            httpPut.setEntity(reqEntity);

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 201 || statusCode == 200 || statusCode == 204) {
                    return uploadUrl;
                } else {
                    throw new IOException("Failed to upload file to Nextcloud. Status code: " + statusCode);
                }
            }
        }
    }



    /*private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }*/

    private void uploadToNextcloud(File file, String remoteFilePath) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(baseUrl + "/remote.php/dav/files/" + username + "/" + remoteFilePath);

            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpPut.setHeader("Authorization", "Basic " + encodedAuth);

            FileEntity fileEntity = new FileEntity(file);
            httpPut.setEntity(fileEntity);

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
            }
        }
    }


    public Map<String, byte[]> downloadImages(Integer ticketNo, String role, String companyId, String siteId,String truckStatus) {
        if (role == null) {
            throw new ResourceNotFoundException("role is not given");
        }
        Integer roleId = roleMasterRepository.findRoleIdByRoleName(role);
        if (roleId == null) {
            throw new ResourceNotFoundException("role is not found " + role);
        }
        CameraView cameraView = cameraRepository.findByTicketNoAndRoleIdAndTruckStatus(ticketNo, roleId,truckStatus);
        if (cameraView == null) {
            throw new ResourceNotFoundException("CameraView not found for ticketNo and role: " + ticketNo + " and " + role);
        }

        Map<String, byte[]> imagesMap = new HashMap<>();

        // List of image URLs
        Map<String, String> imageUrls = new HashMap<>();
        imageUrls.put("frontImg1", cameraView.getFrontImg1());
        imageUrls.put("backImg2", cameraView.getBackImg2());
        imageUrls.put("topImg3", cameraView.getTopImg3());
        imageUrls.put("bottomImg4", cameraView.getBottomImg4());
        imageUrls.put("leftImg5", cameraView.getLeftImg5());
        imageUrls.put("rightImg6", cameraView.getRightImg6());
        // Add other images if necessary

        for (Map.Entry<String, String> entry : imageUrls.entrySet()) {
            String imageType = entry.getKey();
            String remoteFilePath = entry.getValue();

            if (remoteFilePath != null && !remoteFilePath.isEmpty()) {
                try {
                    // Extract only the file name from the URL
                    String fileName = remoteFilePath.substring(remoteFilePath.lastIndexOf('/') + 1);
                    System.out.println("Extracted file name: " + fileName);
                    String adjustedRemoteFilePath = "remote.php/dav/files/" + username + "/" + fileName;

                    // Download the file using the adjusted path
                    byte[] fileBytes = downloadFromNextcloud(remoteFilePath);
                    imagesMap.put(imageType, fileBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle or log the exception as needed
                }
            } else {
                // Handle case where remoteFilePath is null or empty
                // You can choose to log a message or perform any other action
                System.out.println("Skipping download for " + imageType + " because remoteFilePath is null or empty");
            }
        }

        return imagesMap;
    }


    private byte[] downloadFromNextcloud(String remoteFilePath) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(remoteFilePath);
            System.out.println("Downloading from: " + remoteFilePath);

            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpGet.setHeader("Authorization", "Basic " + encodedAuth);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toByteArray(response.getEntity());
            }
        }
    }


}
