package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.MaterialTypeMaster;
import com.weighbridge.admin.entities.QualityRange;
import com.weighbridge.admin.payloads.MaterialWithParametersRequest;
import com.weighbridge.admin.payloads.MaterialWithParametersRequest.Parameter;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.MaterialTypeMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeRepository;
import com.weighbridge.admin.services.MaterialMasterService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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


//    @Override
//    public String createMaterialWithParameterAndRange(MaterialWithParametersRequest materialWithParametersRequest) {
//        MaterialMaster materialMaster = materialMasterRepository.findByMaterialName(materialWithParametersRequest.getMaterialName());
//        MaterialMaster saved = null;
//        if (materialMaster == null) {
//            materialMaster = new MaterialMaster();
//            materialMaster.setMaterialName(materialWithParametersRequest.getMaterialName());
//            saved = materialMasterRepository.save(materialMaster);
//        }
//
//        MaterialTypeMaster materialTypeMaster = materialTypeMasterRepository.findByMaterialTypeName(materialWithParametersRequest.getMaterialTypeName());
//        if (materialTypeMaster == null) {
//            materialTypeMaster = new MaterialTypeMaster();
//            materialTypeMaster.setMaterialTypeName(materialWithParametersRequest.getMaterialTypeName());
//            materialTypeMaster.setMaterialMaster(saved);
//            materialTypeMasterRepository.save(materialTypeMaster);
//        }
//
//        List<QualityRange> qualityRanges = new ArrayList<>();
//        for (Parameter rangeRequest : materialWithParametersRequest.getParameters()) {
//            QualityRange qualityRange = new QualityRange();
//            qualityRange.setParameterName(rangeRequest.getParameterName());
//            qualityRange.setRangeFrom(rangeRequest.getRangeFrom());
//            qualityRange.setRangeTo(rangeRequest.getRangeTo());
//            qualityRange.setMaterialTypeMaster(materialTypeMaster);
//            qualityRanges.add(qualityRange);
//        }
//
//        qualityRangeRepository.saveAll(qualityRanges);
//        return "Data saved successfully";
//    }

    @Override
    public String createMaterialWithParameterAndRange(MaterialWithParametersRequest request) {
        MaterialMaster materialMaster = materialMasterRepository.findByMaterialName(request.getMaterialName());
        if (materialMaster == null) {
            materialMaster = new MaterialMaster();
            materialMaster.setMaterialName(request.getMaterialName());
            materialMaster = materialMasterRepository.save(materialMaster);
        }

        MaterialTypeMaster materialTypeMaster = materialTypeMasterRepository.findByMaterialTypeName(request.getMaterialTypeName());
        if (materialTypeMaster == null) {
            materialTypeMaster = new MaterialTypeMaster();
            materialTypeMaster.setMaterialTypeName(request.getMaterialTypeName());
            materialTypeMaster.setMaterialMaster(materialMaster);
            materialTypeMasterRepository.save(materialTypeMaster);
        }

        List<QualityRange> qualityRanges = createQualityRanges(request.getParameters(), materialTypeMaster);
        qualityRangeRepository.saveAll(qualityRanges);
        return "Data saved successfully";
    }

    @Override
    public List<String> getTypeWithMaterial(String materialName) {
        List<String> allMaterialTypeNames = materialTypeMasterRepository.findByMaterialMasterMaterialName(materialName);
        return allMaterialTypeNames;
    }

    private List<QualityRange> createQualityRanges(List<Parameter> parameters, MaterialTypeMaster materialTypeMaster) {
        return parameters.stream()
                .map(parameter -> {
                    QualityRange qualityRange = new QualityRange();
                    qualityRange.setParameterName(parameter.getParameterName());
                    qualityRange.setRangeFrom(parameter.getRangeFrom());
                    qualityRange.setRangeTo(parameter.getRangeTo());
                    qualityRange.setMaterialTypeMaster(materialTypeMaster);
                    return qualityRange;
                })
                .collect(Collectors.toList());
    }


}
