package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.services.MaterialMasterService;
import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialMasterServiceImpl implements MaterialMasterService {

    private MaterialMasterRepository materialMasterRepository;

    private ModelMapper modelMapper;
    private HttpServletRequest httpServletRequest;

    public MaterialMasterServiceImpl(MaterialMasterRepository materialMasterRepository, ModelMapper modelMapper, HttpServletRequest httpServletRequest) {
        this.materialMasterRepository = materialMasterRepository;
        this.modelMapper = modelMapper;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public MaterialMasterDto saveMaterials(MaterialMasterDto materialMasterDto) {
        // Convert dto to entity
        MaterialMaster material = new MaterialMaster();
        material.setMaterialName(materialMasterDto.getMaterialName());

        // Check if there exists a material with the given name
        if(materialMasterRepository.existsByMaterialName(materialMasterDto.getMaterialName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Material is already present.");
        }

        HttpSession session = httpServletRequest.getSession();
        String loggedInUser = session.getAttribute("userId").toString(); // Assuming the user creation is done by the current session user
        material.setMaterialCreatedBy(loggedInUser);
        material.setMaterialCreatedDate(LocalDateTime.now());
        material.setMaterialModifiedBy(loggedInUser);
        material.setMaterialModifiedDate(LocalDateTime.now());

        // Save materials
        MaterialMaster savedMaterial = materialMasterRepository.save(material);
        return modelMapper.map(savedMaterial, MaterialMasterDto.class);
    }

    @Override
    public List<MaterialMasterDto> getAllMaterials() {
        // Fetch all materials
        List<MaterialMaster> listOfMaterials = materialMasterRepository.findAll();

        return listOfMaterials.stream().map(materialMaster -> modelMapper.map(materialMaster, MaterialMasterDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMaterialNames() {
        List<String> listOfMaterialNames = materialMasterRepository.findAllMaterialNameByMaterialStatus("ACTIVE");

        return listOfMaterialNames;
    }

    @Override
    public void deleteMaterial(String materialName) {
        MaterialMaster materialMaster = materialMasterRepository.findByMaterialName(materialName);
        if (materialMaster.getMaterialStatus().equals("ACTIVE")){
            materialMaster.setMaterialStatus("INACTIVE");
        }
        materialMasterRepository.save(materialMaster);
    }
}
