package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.repsitories.AcceptableQualityRangeRepository;
import com.weighbridge.admin.services.AcceptableQualityRangeService;
import org.springframework.stereotype.Service;

@Service
public class AcceptableQualityRangeServiceImpl implements AcceptableQualityRangeService {
    private final AcceptableQualityRangeRepository acceptableQualityRangeRepository;

    public AcceptableQualityRangeServiceImpl(AcceptableQualityRangeRepository acceptableQualityRangeRepository) {
        this.acceptableQualityRangeRepository = acceptableQualityRangeRepository;
    }
}
