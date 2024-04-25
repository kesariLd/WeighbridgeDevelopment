package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.SequenceGenerator;
import com.weighbridge.admin.entities.SequenceGeneratorPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SequenceGeneratorRepository extends JpaRepository<SequenceGenerator, SequenceGeneratorPK> {


    Optional<SequenceGenerator> findByCompanyIdAndSiteId(String companyId, String siteId);
}
