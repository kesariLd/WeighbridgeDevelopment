package com.weighbridge.management.services.impl;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.services.ManagementDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Service
public class ManagementDashboardServiceImpl implements ManagementDashboardService {

    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;

  /*  public Long getInboundCount(ManagementPayload managementPayload){
        gateEntryTransactionRepository.countInbounddetails
        return null;
    }*/
}