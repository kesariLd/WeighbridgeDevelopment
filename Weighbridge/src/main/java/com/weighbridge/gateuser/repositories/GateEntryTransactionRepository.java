package com.weighbridge.gateuser.repositories;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.util.List;

/**
 * Repository interface for accessing gate entry transaction data.
 */
public interface GateEntryTransactionRepository extends JpaRepository<GateEntryTransaction, Integer> {

    /**
     * Retrieves a list of gate entry transactions based on the specified site ID and company ID.
     *
     * @param siteId    The ID of the site to filter gate entry transactions.
     * @param companyId The ID of the company to filter gate entry transactions.
     * @return A list of gate entry transactions matching the provided site ID and company ID.
     */
    List<GateEntryTransaction> findBySiteIdAndCompanyIdOrderByTicketNoDesc(String siteId, String companyId);

    GateEntryTransaction findByTicketNo(Integer ticketNo);

//    List<GateEntryTransaction> findBySiteIdAndCompanyId(String userSite, String userCompany);
}

