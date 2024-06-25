package com.weighbridge.camera.controller;



import com.weighbridge.camera.services.CameraViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/v1/camera")
public class UploadController {

    @Value("${nextcloud.base-url}")
    private String baseUrl;

    @Value("${nextcloud.username}")
    private String username;

    @Value("${nextcloud.password}")
    private String password;

    @Autowired
    private CameraViewService cameraService;



    @PostMapping("/upload")
    public ResponseEntity<String> uploadImages(
            @RequestParam("ticketNo") Integer ticketNo,
            @RequestParam("frontImg1") MultipartFile frontImg1,
            @RequestParam("backImg2") MultipartFile backImg2,
            @RequestParam(value = "topImg3",required = false) MultipartFile topImg3,
            @RequestParam(value = "bottomImg4",required = false) MultipartFile bottomImg4,
            @RequestParam(value = "leftImg5",required = false) MultipartFile leftImg5,
            @RequestParam(value = "rightImg6",required = false) MultipartFile rightImg6,
            @RequestParam("role") String role) throws IOException {

        String response = cameraService.uploadImages(ticketNo, frontImg1, backImg2, topImg3, bottomImg4, leftImg5, rightImg6, role,"IN");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        File convFile = convertMultipartFileToFile(file);
        uploadToNextcloud(convFile, file.getOriginalFilename());
        return "File uploaded successfully!";
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private void uploadToNextcloud(File file, String remoteFilePath) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut(baseUrl + "/remote.php/dav/files?dir=/Weighbridge/" + username + "/" + remoteFilePath);

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
    @GetMapping("/get")
    public ResponseEntity<Map<String, byte[]>> downloadImages(
            @RequestParam Integer ticketNo,
            @RequestParam String role,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String siteId,
            @RequestParam(required = false) String truckStatus) {

        Map<String, byte[]> imagesMap = cameraService.downloadImages(ticketNo, role, companyId, siteId,truckStatus);


        return ResponseEntity.ok()
                .body(imagesMap);
    }




   /* @GetMapping("/{filePath:.+}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filePath) throws IOException {
        byte[] fileBytes = downloadFromNextcloud(filePath);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filePath.substring(filePath.lastIndexOf('/') + 1));

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(fileBytes.length)
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(byteArrayInputStream));
    }

    private byte[] downloadFromNextcloud(String remoteFilePath) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(baseUrl + "/remote.php/dav/files/" + username + "/" + remoteFilePath);
            System.out.println("==========="+baseUrl + "/remote.php/dav/files/" + username + "/" + remoteFilePath);
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            httpGet.setHeader("Authorization", "Basic " + encodedAuth);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                return EntityUtils.toByteArray(response.getEntity());
            }
        }
    }*/
}
