package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.MaterialTypeMaster;
import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.payloads.MaterialWithParameters;
import com.weighbridge.admin.payloads.MaterialWithParameters.Parameter;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.MaterialTypeMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeMasterRepository;
import com.weighbridge.admin.services.MaterialMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the MaterialMasterService interface
 */
@Slf4j
@Service
public class MaterialMasterServiceImpl implements MaterialMasterService {
    private final MaterialMasterRepository materialMasterRepository;
    private final ModelMapper modelMapper;
    private final HttpServletRequest httpServletRequest;
    private final MaterialTypeMasterRepository materialTypeMasterRepository;
    private final QualityRangeMasterRepository qualityRangeMasterRepository;

    public MaterialMasterServiceImpl(MaterialMasterRepository materialMasterRepository, ModelMapper modelMapper, HttpServletRequest httpServletRequest, MaterialTypeMasterRepository materialTypeMasterRepository, QualityRangeMasterRepository qualityRangeMasterRepository) {
        this.materialMasterRepository = materialMasterRepository;
        this.modelMapper = modelMapper;
        this.httpServletRequest = httpServletRequest;
        this.materialTypeMasterRepository = materialTypeMasterRepository;
        this.qualityRangeMasterRepository = qualityRangeMasterRepository;
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


    @Override
    public String createMaterialWithParameterAndRange(MaterialWithParameters request) {
        HttpSession session = httpServletRequest.getSession();
        String user = session.getAttribute("userId").toString();
        LocalDateTime currentDateTime = LocalDateTime.now();
        MaterialMaster materialMaster = materialMasterRepository.findByMaterialName(request.getMaterialName());
        if (materialMaster == null) {
            materialMaster = new MaterialMaster();
            materialMaster.setMaterialName(request.getMaterialName());
            materialMaster.setMaterialCreatedBy(user);
            materialMaster.setMaterialCreatedDate(currentDateTime);
            materialMaster.setMaterialModifiedBy(user);
            materialMaster.setMaterialModifiedDate(currentDateTime);
            materialMaster = materialMasterRepository.save(materialMaster);
        }

        MaterialTypeMaster materialTypeMaster = materialTypeMasterRepository.findByMaterialTypeName(request.getMaterialTypeName());
        if (request.getMaterialTypeName() != null && materialTypeMaster == null) {
            materialTypeMaster = new MaterialTypeMaster();
            materialTypeMaster.setMaterialTypeName(request.getMaterialTypeName());
            materialTypeMaster.setMaterialMaster(materialMaster);
            materialTypeMasterRepository.save(materialTypeMaster);
        }

        List<QualityRangeMaster> qualityRangeMasters = createQualityRanges(request.getParameters(), materialMaster);
        qualityRangeMasterRepository.saveAll(qualityRangeMasters);
        return "Data saved successfully";
    }

    @Override
    public List<String> getTypeWithMaterial(String materialName) {
        List<String> allMaterialTypeNames = materialTypeMasterRepository.findByMaterialMasterMaterialName(materialName);
        return allMaterialTypeNames;
    }

    @Override
    public List<MaterialWithParameters> getQualityRangesByMaterialName(String materialName) {
        List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByMaterialMasterMaterialName(materialName);
        return mapQualityRangesToMaterialWithParameters(qualityRangeMasters);
    }

    private List<MaterialWithParameters> mapQualityRangesToMaterialWithParameters(List<QualityRangeMaster> qualityRangeMasters) {
        Map<String, MaterialWithParameters> materialWithParametersMap = new HashMap<>();
        for (QualityRangeMaster qualityRangeMaster : qualityRangeMasters) {
            String key = qualityRangeMaster.getMaterialMaster().getMaterialName();
            MaterialWithParameters materialWithParameters = materialWithParametersMap.get(key);
            if (materialWithParameters == null) {
                materialWithParameters = new MaterialWithParameters();
                materialWithParameters.setMaterialName(qualityRangeMaster.getMaterialMaster().getMaterialName());
                materialWithParameters.setMaterialTypeName(null);
                materialWithParameters.setParameters(new ArrayList<>());
                materialWithParametersMap.put(key, materialWithParameters);
            }

            MaterialWithParameters.Parameter parameter = new MaterialWithParameters.Parameter();
            parameter.setParameterName(qualityRangeMaster.getParameterName());
            parameter.setRangeFrom(qualityRangeMaster.getRangeFrom());
            parameter.setRangeTo(qualityRangeMaster.getRangeTo());

            materialWithParameters.getParameters().add(parameter);
        }

        return new ArrayList<>(materialWithParametersMap.values());
    }


    private List<QualityRangeMaster> createQualityRanges(List<Parameter> parameters, MaterialMaster materialMaster) {

        return parameters.stream()
                .map(parameter -> {
                    QualityRangeMaster qualityRangeMaster = new QualityRangeMaster();
                    if(!qualityRangeMasterRepository.existsByParameterNameAndMaterialMasterMaterialId(parameter.getParameterName(),materialMaster.getMaterialId())) {
                        qualityRangeMaster.setParameterName(parameter.getParameterName());
                        qualityRangeMaster.setRangeFrom(parameter.getRangeFrom());
                        qualityRangeMaster.setRangeTo(parameter.getRangeTo());
                        qualityRangeMaster.setMaterialMaster(materialMaster);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Parameter is already set");
                    }
                    return qualityRangeMaster;
                })
                .collect(Collectors.toList());
    }


}
