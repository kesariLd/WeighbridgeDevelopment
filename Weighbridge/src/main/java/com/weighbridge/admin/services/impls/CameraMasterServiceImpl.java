package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.CameraMasterDto;
import com.weighbridge.admin.entities.CameraMaster;
import com.weighbridge.admin.entities.RoleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.CameraMasterRepository;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.RoleMasterRepository;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.admin.services.CameraMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
