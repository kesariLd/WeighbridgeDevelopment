package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.AcceptableQualityRange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcceptableQualityRangeRepository extends JpaRepository<AcceptableQualityRange, Long> {
}

