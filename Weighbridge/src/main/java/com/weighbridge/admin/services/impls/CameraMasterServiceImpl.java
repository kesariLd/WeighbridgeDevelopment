package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.CameraMasterDto;
import com.weighbridge.admin.entities.CameraMaster;
import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;


import com.weighbridge.admin.payloads.CameraMasterResponse;


import com.weighbridge.admin.repsitories.CameraMasterRepository;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.RoleMasterRepository;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.admin.services.CameraMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




@Service
public class CameraMasterServiceImpl implements CameraMasterService {

    @Autowired
    private CameraMasterRepository cameraMasterRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private SiteMasterRepository siteMasterRepository;

    @Autowired
    private RoleMasterRepository roleMasterRepository;



    @Override
    public String saveCameraUrl(CameraMasterDto cameraMasterDto,String userId) {
        String companyId = companyMasterRepository.findCompanyIdByCompanyName(cameraMasterDto.getCompanyName());
        String[] siteInfoParts = cameraMasterDto.getSiteName().split(",", 2);
        String siteName=null;
        String siteAddress="";
        if (siteInfoParts.length != 2) {
            //throw new IllegalArgumentException("Invalid format for site info: " + userRequest.getSite());
            siteName = siteInfoParts[0].trim();
        }
        else {
            siteName = siteInfoParts[0].trim();
            siteAddress = siteInfoParts[1].trim();
        }
        String siteId = siteMasterRepository.findSiteIdBySiteName(siteName, siteAddress);
        RoleMaster roleMaster = roleMasterRepository.findByRoleName(cameraMasterDto.getRole());
        CameraMaster cameraMaster=new CameraMaster();
        cameraMaster.setCompanyId(companyId);
        cameraMaster.setSiteId(siteId);
        if (roleMaster != null) {
            cameraMaster.setRoleId(roleMaster.getRoleId());
        } else {
            throw new ResourceNotFoundException("role doesnot exist!");
        }
        cameraMaster.setFrontCamUrl3(cameraMasterDto.getFrontCamUrl3());
        cameraMaster.setBackCamUrl4(cameraMasterDto.getBackCamUrl4());
        cameraMaster.setBottomCamUrl2(cameraMasterDto.getBottomCamUrl2());
        cameraMaster.setTopCamUrl1(cameraMasterDto.getTopCamUrl1());
        cameraMaster.setLeftCamUrl5(cameraMasterDto.getLeftCamUrl5());
        cameraMaster.setRightCamUrl6(cameraMasterDto.getRightCamUrl6());
        LocalDateTime localDateTime=LocalDateTime.now();
        cameraMaster.setCreatedBy(userId);
        cameraMaster.setModifiedBy(userId);
        cameraMaster.setCreatedDate(localDateTime);
        cameraMaster.setModifiedDate(localDateTime);
        CameraMaster save = cameraMasterRepository.save(cameraMaster);
        return "Camera Details saved for "+cameraMasterDto.getRole()+" with Id "+save.getId();
    }

    @Override
    public List<CameraMasterResponse> getCameraDetails() {
        List<CameraMaster> all = cameraMasterRepository.findAll();
        List<CameraMasterResponse> list=new ArrayList<>();
        for(CameraMaster cameraMasterDto:all){
            String companyNameByCompanyId = companyMasterRepository.findCompanyNameByCompanyId(cameraMasterDto.getCompanyId());
            String siteName=siteMasterRepository.findSiteNameBySiteId(cameraMasterDto.getSiteId());
            RoleMaster byId = roleMasterRepository.findById(cameraMasterDto.getRoleId()).get();
            CameraMasterResponse cameraMasterDto1=new CameraMasterResponse();

            cameraMasterDto1.setCameraId(cameraMasterDto.getId());

            cameraMasterDto1.setCompanyName(companyNameByCompanyId);
            cameraMasterDto1.setSiteName(siteName);
            if(byId!=null) {
                cameraMasterDto1.setRole(byId.getRoleName());
            }
            else{
                throw new ResourceNotFoundException("role not found.");
            }
            cameraMasterDto1.setFrontCamUrl3(cameraMasterDto.getFrontCamUrl3());
            cameraMasterDto1.setBackCamUrl4(cameraMasterDto.getBackCamUrl4());
            cameraMasterDto1.setLeftCamUrl5(cameraMasterDto.getLeftCamUrl5());
            cameraMasterDto1.setRightCamUrl6(cameraMasterDto.getRightCamUrl6());
            cameraMasterDto1.setTopCamUrl1(cameraMasterDto.getTopCamUrl1());
            cameraMasterDto1.setBottomCamUrl2(cameraMasterDto.getBottomCamUrl2());
            list.add(cameraMasterDto1);
        }
        return list;
    }

    @Override
    public CameraMasterResponse getCameraDetail(Long id) {
        CameraMaster cameraMaster = cameraMasterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("camera with id " + id + " not found"));
        String companyNameByCompanyId = companyMasterRepository.findCompanyNameByCompanyId(cameraMaster.getCompanyId());
        SiteMaster byId1 = siteMasterRepository.findById(cameraMaster.getSiteId()).get();
        RoleMaster byId = roleMasterRepository.findById(cameraMaster.getRoleId()).get();
        CameraMasterResponse cameraMasterResponse=new CameraMasterResponse();
        cameraMasterResponse.setCameraId(cameraMaster.getId());
        cameraMasterResponse.setCompanyName(companyNameByCompanyId);
        if(byId1!=null) {
            String siteAddress = byId1.getSiteAddress();
            String siteName = byId1.getSiteName();
            String site = siteName + ", " + siteAddress;
            cameraMasterResponse.setSiteName(site);
        }
        if(byId!=null) {
            cameraMasterResponse.setRole(byId.getRoleName());
        }
        else{
            throw new ResourceNotFoundException("role not found.");
        }
        cameraMasterResponse.setTopCamUrl1(cameraMaster.getTopCamUrl1());
        cameraMasterResponse.setBottomCamUrl2(cameraMaster.getBottomCamUrl2());
        cameraMasterResponse.setFrontCamUrl3(cameraMaster.getFrontCamUrl3());
        cameraMasterResponse.setBackCamUrl4(cameraMaster.getBackCamUrl4());
        cameraMasterResponse.setLeftCamUrl5(cameraMaster.getLeftCamUrl5());
        cameraMasterResponse.setRightCamUrl6(cameraMaster.getRightCamUrl6());
        return cameraMasterResponse;
    }

    public String updateCameraDetails(CameraMasterResponse cameraMasterDto,Long id,String userId){
        CameraMaster cameraMaster = cameraMasterRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("cameraDetail with id " + id + " not found"));
        cameraMaster.setRightCamUrl6(cameraMasterDto.getRightCamUrl6());
        cameraMaster.setLeftCamUrl5(cameraMasterDto.getLeftCamUrl5());
        cameraMaster.setTopCamUrl1(cameraMasterDto.getTopCamUrl1());
        cameraMaster.setBottomCamUrl2(cameraMasterDto.getBottomCamUrl2());
        cameraMaster.setFrontCamUrl3(cameraMasterDto.getFrontCamUrl3());
        cameraMaster.setBackCamUrl4(cameraMasterDto.getBackCamUrl4());
        String companyId = companyMasterRepository.findCompanyIdByCompanyName(cameraMasterDto.getCompanyName());
        String[] siteInfoParts = cameraMasterDto.getSiteName().split(",", 2);
        String siteName=null;
        String siteAddress="";
        if (siteInfoParts.length != 2) {
            //throw new IllegalArgumentException("Invalid format for site info: " + userRequest.getSite());
            siteName = siteInfoParts[0].trim();
        }
        else {
            siteName = siteInfoParts[0].trim();
            siteAddress = siteInfoParts[1].trim();
        }
        String siteId = siteMasterRepository.findSiteIdBySiteName(siteName, siteAddress);
        RoleMaster roleMaster = roleMasterRepository.findByRoleName(cameraMasterDto.getRole());
        cameraMaster.setSiteId(siteId);
        cameraMaster.setCompanyId(companyId);
        if (roleMaster != null) {
            cameraMaster.setRoleId(roleMaster.getRoleId());
        } else {
            throw new ResourceNotFoundException("role doesnot exist!");
        }
        LocalDateTime localDateTime=LocalDateTime.now();
        cameraMaster.setModifiedBy(userId);
        cameraMaster.setModifiedDate(localDateTime);
        cameraMasterRepository.save(cameraMaster);
        return "camera details updated Successfully";
    }

    @Override
    public String deleteCameraDetails(Long id) {

        if(cameraMasterRepository.existsById(id)){
            cameraMasterRepository.deleteById(id);
            return "record deleted with id "+id;
        }
        else{
            return "record not found.";
        }
    }

   
    }


