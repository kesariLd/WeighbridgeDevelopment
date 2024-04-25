package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.List;

public interface GateEntryTransactionRepository extends JpaRepository<GateEntryTransaction,Integer> {

    List<GateEntryTransaction> findBySiteIdAndCompanyId(String siteId,String companyId);
}
