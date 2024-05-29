package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.QualityRangeMasterDto;
import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.repsitories.QualityRangeMasterRepository;
import com.weighbridge.admin.services.QualityRangeMasterService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualityRangeMasterServiceImpl implements QualityRangeMasterService {

    private final QualityRangeMasterRepository qualityRangeMasterRepository;
    private final ModelMapper modelMapper;

    public QualityRangeMasterServiceImpl(QualityRangeMasterRepository qualityRangeMasterRepository, ModelMapper modelMapper) {
        this.qualityRangeMasterRepository = qualityRangeMasterRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<QualityRangeMasterDto> getAllQualityRangesForMaterial() {
        List<QualityRangeMaster> qualityRangeMasterList = qualityRangeMasterRepository.findByMaterialMasterIsNotNull();
        return qualityRangeMasterList.stream().map(qualityRangeMaster ->
                modelMapper.map(qualityRangeMaster, QualityRangeMasterDto.class)).collect(Collectors.toList());
    }
}
