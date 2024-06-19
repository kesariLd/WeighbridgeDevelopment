package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.MaterialTypeMaster;
import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.payloads.MaterialAndTypeRequest;
import com.weighbridge.admin.payloads.MaterialParameterResponse;
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

        return listOfMaterials.stream().map(
                materialMaster -> {
                    MaterialMasterDto materialMasterDto = modelMapper.map(materialMaster, MaterialMasterDto.class);
                    List<String> materialTypeNames = materialTypeMasterRepository.findByMaterialMasterMaterialName(materialMaster.getMaterialName());
                    String joinedMateriTypeName = String.join(", ", materialTypeNames);
                    materialMasterDto.setMaterialTypeName(joinedMateriTypeName);
                    return materialMasterDto;
                }
        ).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMaterialNames() {
        return materialMasterRepository.findAllMaterialNameByMaterialStatus("ACTIVE");
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
//        HttpSession session = httpServletRequest.getSession();
//        String user = session.getAttribute("userId").toString();
//        LocalDateTime currentDateTime = LocalDateTime.now();

        MaterialMaster materialMaster = materialMasterRepository.findByMaterialName(request.getMaterialName());
//        if (materialMaster == null) {
//            materialMaster = new MaterialMaster();
//            materialMaster.setMaterialName(request.getMaterialName());
//            materialMaster.setMaterialCreatedBy(user);
//            materialMaster.setMaterialCreatedDate(currentDateTime);
//            materialMaster.setMaterialModifiedBy(user);
//            materialMaster.setMaterialModifiedDate(currentDateTime);
//            materialMaster = materialMasterRepository.save(materialMaster);
//        }

//        MaterialTypeMaster materialTypeMaster = materialTypeMasterRepository.findByMaterialTypeName(request.getMaterialTypeName());
//        if (request.getMaterialTypeName() != null) {
//            MaterialTypeMaster materialTypeMaster = new MaterialTypeMaster();
//            materialTypeMaster.setMaterialTypeName(request.getMaterialTypeName());
//            materialTypeMaster.setMaterialMaster(materialMaster);
//            materialTypeMasterRepository.save(materialTypeMaster);
//        }

        List<QualityRangeMaster> qualityRangeMasters = createQualityRanges(request.getParameters(), materialMaster,request.getSupplierName(), request.getSupplierAddress());
        qualityRangeMasterRepository.saveAll(qualityRangeMasters);
        return "Parameter for " + request.getMaterialName() + " saved successfully";
    }

    @Override
    public List<String> getTypeWithMaterial(String materialName) {
        return materialTypeMasterRepository.findByMaterialMasterMaterialName(materialName);
    }

    @Override
    public List<MaterialWithParameters> getQualityRangesByMaterialNameAndSupplierNameAndAddress(String materialName, String supplierName, String supplierAddress) {
        List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(materialName, supplierName, supplierAddress);
        return mapQualityRangesToMaterialWithParameters(qualityRangeMasters, supplierName, supplierAddress);
    }

    @Override
    public String saveMaterialAndMaterialType(MaterialAndTypeRequest request,String userId) {

        if(userId==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Please Provide userId");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        MaterialMaster materialMaster = materialMasterRepository.findByMaterialName(request.getMaterialName());
        if (materialMaster == null) {
            materialMaster = new MaterialMaster();
            materialMaster.setMaterialName(request.getMaterialName());
            materialMaster.setMaterialCreatedBy(userId);
            materialMaster.setMaterialCreatedDate(currentDateTime);
            materialMaster.setMaterialModifiedBy(userId);
            materialMaster.setMaterialModifiedDate(currentDateTime);
            materialMaster = materialMasterRepository.save(materialMaster);
        }

//        MaterialTypeMaster materialTypeMaster = materialTypeMasterRepository.findByMaterialTypeName(request.getMaterialTypeName());
        if (request.getMaterialTypeName() != null) {
            Boolean isExists = materialTypeMasterRepository
                    .existsByMaterialTypeNameAndMaterialMasterMaterialId(request.getMaterialTypeName(), materialMaster.getMaterialId());
            if (isExists) {
                throw new ResponseStatusException(HttpStatus.FOUND, "Material type \"" + request.getMaterialTypeName() + "\" already exists !");
            }
            MaterialTypeMaster materialTypeMaster = new MaterialTypeMaster();
            materialTypeMaster.setMaterialTypeName(request.getMaterialTypeName());
            materialTypeMaster.setMaterialMaster(materialMaster);
            materialTypeMasterRepository.save(materialTypeMaster);
        }
        return request.getMaterialName() + " is saved Successfully";
    }

    @Override
    public List<MaterialWithParameters> getMaterialParameters(String materialName) {
        List<QualityRangeMaster> qualityRangeMasterList = qualityRangeMasterRepository.findByMaterialMasterMaterialName(materialName);
        Map<String, MaterialWithParameters> materialMap = new HashMap<>();

        for (QualityRangeMaster qualityRangeMaster : qualityRangeMasterList) {
            String key = qualityRangeMaster.getMaterialMaster().getMaterialName() + "|" +
                    qualityRangeMaster.getSupplierName() + "|" +
                    qualityRangeMaster.getSupplierAddress();

            MaterialWithParameters materialWithParameters = materialMap.get(key);
            if (materialWithParameters == null) {
                materialWithParameters = new MaterialWithParameters();
                materialWithParameters.setMaterialName(qualityRangeMaster.getMaterialMaster().getMaterialName());
                materialWithParameters.setSupplierName(qualityRangeMaster.getSupplierName());
                materialWithParameters.setSupplierAddress(qualityRangeMaster.getSupplierAddress());
                materialWithParameters.setParameters(new ArrayList<>());
                materialMap.put(key, materialWithParameters);
            }

            MaterialWithParameters.Parameter parameter = new MaterialWithParameters.Parameter();
            parameter.setParameterName(qualityRangeMaster.getParameterName());
            parameter.setRangeFrom(qualityRangeMaster.getRangeFrom());
            parameter.setRangeTo(qualityRangeMaster.getRangeTo());

            materialWithParameters.getParameters().add(parameter);
        }

        return new ArrayList<>(materialMap.values());
    }


    private List<MaterialWithParameters> mapQualityRangesToMaterialWithParameters(List<QualityRangeMaster> qualityRangeMasters, String supplierName, String supplierAddress) {
        Map<String, MaterialWithParameters> materialWithParametersMap = new HashMap<>();
        for (QualityRangeMaster qualityRangeMaster : qualityRangeMasters) {
            String key = qualityRangeMaster.getMaterialMaster().getMaterialName();
            MaterialWithParameters materialWithParameters = materialWithParametersMap.get(key);
            if (materialWithParameters == null) {
                materialWithParameters = new MaterialWithParameters();
                materialWithParameters.setMaterialName(qualityRangeMaster.getMaterialMaster().getMaterialName());
//                materialWithParameters.setMaterialTypeName(null);
                materialWithParameters.setSupplierName(supplierName);
                materialWithParameters.setSupplierAddress(supplierAddress);
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


    private List<QualityRangeMaster> createQualityRanges(List<Parameter> parameters, MaterialMaster materialMaster, String supplierName, String supplierAddress) {

        return parameters.stream()
                .map(parameter -> {
                    QualityRangeMaster qualityRangeMaster = new QualityRangeMaster();
                    if(!qualityRangeMasterRepository.existsByParameterNameAndMaterialMasterMaterialIdAndSupplierNameAndSupplierAddress(parameter.getParameterName(),materialMaster.getMaterialId(), supplierName, supplierAddress)) {
                        qualityRangeMaster.setParameterName(parameter.getParameterName());
                        qualityRangeMaster.setRangeFrom(parameter.getRangeFrom());
                        qualityRangeMaster.setRangeTo(parameter.getRangeTo());
                        qualityRangeMaster.setMaterialMaster(materialMaster);
                        qualityRangeMaster.setSupplierName(supplierName);
                        qualityRangeMaster.setSupplierAddress(supplierAddress);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,parameter.getParameterName() + " is already set");
                    }
                    return qualityRangeMaster;
                })
                .collect(Collectors.toList());
    }
}