package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.MaterialTypeMaster;
import com.weighbridge.admin.entities.QualityRange;
import com.weighbridge.admin.payloads.MaterialWithParameters;
import com.weighbridge.admin.payloads.MaterialWithParameters.Parameter;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.MaterialTypeMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeRepository;
import com.weighbridge.admin.services.MaterialMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    private final QualityRangeRepository qualityRangeRepository;

    public MaterialMasterServiceImpl(MaterialMasterRepository materialMasterRepository, ModelMapper modelMapper, HttpServletRequest httpServletRequest, MaterialTypeMasterRepository materialTypeMasterRepository, QualityRangeRepository qualityRangeRepository) {
        this.materialMasterRepository = materialMasterRepository;
        this.modelMapper = modelMapper;
        this.httpServletRequest = httpServletRequest;
        this.materialTypeMasterRepository = materialTypeMasterRepository;
        this.qualityRangeRepository = qualityRangeRepository;
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
        if (materialTypeMaster == null) {
            materialTypeMaster = new MaterialTypeMaster();
            materialTypeMaster.setMaterialTypeName(request.getMaterialTypeName());
            materialTypeMaster.setMaterialMaster(materialMaster);
            materialTypeMasterRepository.save(materialTypeMaster);
        }

        List<QualityRange> qualityRanges = createQualityRanges(request.getParameters(), materialTypeMaster, materialMaster);
        qualityRangeRepository.saveAll(qualityRanges);
        return "Data saved successfully";
    }

    @Override
    public List<String> getTypeWithMaterial(String materialName) {
        List<String> allMaterialTypeNames = materialTypeMasterRepository.findByMaterialMasterMaterialName(materialName);
        return allMaterialTypeNames;
    }

    @Override
    public List<MaterialWithParameters> getQualityRangesByMaterialNameAndMaterialTypeName(String materialName, String materialTypeName) {
        List<QualityRange> qualityRanges = qualityRangeRepository.findByMaterialMasterMaterialNameAndMaterialTypeMasterMaterialTypeName(materialName, materialTypeName);
        return mapQualityRangesToMaterialWithParameters(qualityRanges);
    }

    private List<MaterialWithParameters> mapQualityRangesToMaterialWithParameters(List<QualityRange> qualityRanges) {
        Map<String, MaterialWithParameters> materialWithParametersMap = new HashMap<>();
        for (QualityRange qualityRange : qualityRanges) {
            String key = qualityRange.getMaterialMaster().getMaterialName() + "_" +
                    qualityRange.getMaterialTypeMaster().getMaterialTypeName();
            MaterialWithParameters materialWithParameters = materialWithParametersMap.get(key);
            if (materialWithParameters == null) {
                materialWithParameters = new MaterialWithParameters();
                materialWithParameters.setMaterialName(qualityRange.getMaterialMaster().getMaterialName());
                materialWithParameters.setMaterialTypeName(qualityRange.getMaterialTypeMaster().getMaterialTypeName());
                materialWithParameters.setParameters(new ArrayList<>());
                materialWithParametersMap.put(key, materialWithParameters);
            }

            MaterialWithParameters.Parameter parameter = new MaterialWithParameters.Parameter();
            parameter.setParameterName(qualityRange.getParameterName());
            parameter.setRangeFrom(qualityRange.getRangeFrom());
            parameter.setRangeTo(qualityRange.getRangeTo());

            materialWithParameters.getParameters().add(parameter);
        }

        return new ArrayList<>(materialWithParametersMap.values());
    }


    private List<QualityRange> createQualityRanges(List<Parameter> parameters, MaterialTypeMaster materialTypeMaster, MaterialMaster materialMaster) {
        return parameters.stream()
                .map(parameter -> {
                    QualityRange qualityRange = new QualityRange();
                    qualityRange.setParameterName(parameter.getParameterName());
                    qualityRange.setRangeFrom(parameter.getRangeFrom());
                    qualityRange.setRangeTo(parameter.getRangeTo());
                    qualityRange.setMaterialTypeMaster(materialTypeMaster);
                    qualityRange.setMaterialMaster(materialMaster);
                    return qualityRange;
                })
                .collect(Collectors.toList());
    }


}
