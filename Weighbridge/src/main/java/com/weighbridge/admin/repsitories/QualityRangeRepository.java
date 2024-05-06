package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.QualityRange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QualityRangeRepository extends JpaRepository<QualityRange, Long> {
}

