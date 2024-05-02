package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.AcceptableQualityRange;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.ParameterMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.payloads.QualityRangeRequest;
import com.weighbridge.admin.payloads.QualityRangeResponse;
import com.weighbridge.admin.repsitories.AcceptableQualityRangeRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.ParameterMasterRepository;
import com.weighbridge.admin.services.AcceptableQualityRangeService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AcceptableQualityRangeServiceImpl implements AcceptableQualityRangeService {
    private final AcceptableQualityRangeRepository acceptableQualityRangeRepository;
    private final MaterialMasterRepository materialMasterRepository;
    private final ParameterMasterRepository parameterMasterRepository;

    public AcceptableQualityRangeServiceImpl(AcceptableQualityRangeRepository acceptableQualityRangeRepository, MaterialMasterRepository materialMasterRepository, ParameterMasterRepository parameterMasterRepository) {
        this.acceptableQualityRangeRepository = acceptableQualityRangeRepository;
        this.materialMasterRepository = materialMasterRepository;
        this.parameterMasterRepository = parameterMasterRepository;
    }

    @Override
    public Long createQualityRange(QualityRangeRequest qualityRangeRequest) {
        MaterialMaster materialMaster = Optional.ofNullable(materialMasterRepository.findByMaterialName(qualityRangeRequest.getMaterialName())).orElseThrow(() -> new ResourceNotFoundException("Material", "material name" , qualityRangeRequest.getMaterialName() ));
        ParameterMaster parameterMaster = Optional.ofNullable(parameterMasterRepository.findByParameterName(qualityRangeRequest.getParameterName())).orElseThrow(() -> new ResourceNotFoundException("Material", "material name" , qualityRangeRequest.getMaterialName() ));;

        AcceptableQualityRange acceptableQualityRange = new AcceptableQualityRange();
        acceptableQualityRange.setMaterial(materialMaster);
        acceptableQualityRange.setParameter(parameterMaster);
        acceptableQualityRange.setRangeFrom(qualityRangeRequest.getMinValue());
        acceptableQualityRange.setRangeTo(qualityRangeRequest.getMaxValue());

        AcceptableQualityRange savedQualityRange = acceptableQualityRangeRepository.save(acceptableQualityRange);
        return savedQualityRange.getQualityRangeId();
    }

    @Override
    public List<QualityRangeResponse> getQualityRangesByMaterial(String materialName) {
        List<AcceptableQualityRange> allQualityRanges = Optional.ofNullable(acceptableQualityRangeRepository.findByMaterialMaterialName(materialName))
                .orElseThrow(() -> new ResourceNotFoundException("Quality range", "material name", materialName));

        Map<String, QualityRangeResponse> qualityRangeMap = new HashMap<>();
        for (AcceptableQualityRange qualityRange : allQualityRanges) {
            String materialNameKey = qualityRange.getMaterial().getMaterialName();
            QualityRangeResponse qualityRangeResponse = qualityRangeMap.getOrDefault(materialNameKey, new QualityRangeResponse());
            qualityRangeResponse.setMaterialName(materialNameKey);

            List<QualityRangeResponse.ParameterInfo> parameters = qualityRangeResponse.getParameters();
            if (parameters == null) {
                parameters = new ArrayList<>();
            }

            QualityRangeResponse.ParameterInfo parameterInfo = new QualityRangeResponse.ParameterInfo();
            parameterInfo.setParameterName(qualityRange.getParameter().getParameterName());
            parameterInfo.setRangeFrom(qualityRange.getRangeFrom());
            parameterInfo.setRangeTo(qualityRange.getRangeTo());
            parameters.add(parameterInfo);

            qualityRangeResponse.setParameters(parameters);
            qualityRangeMap.put(materialNameKey, qualityRangeResponse);
        }

        return new ArrayList<>(qualityRangeMap.values());
    }


}
