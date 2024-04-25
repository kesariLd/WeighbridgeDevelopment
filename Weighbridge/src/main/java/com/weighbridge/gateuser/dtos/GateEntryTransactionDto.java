package com.weighbridge.gateuser.dtos;

import com.weighbridge.admin.entities.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.util.Date;

@Data
public class GateEntryTransactionDto {
    private String ticketNo;
    private SupplierMaster supplierMaster;
    private TransporterMaster transporterMaster;
    private MaterialMaster materialMaster;
    private VehicleMaster vehicleMaster;
    private SiteMaster siteMaster;

    private CompanyMaster companyMaster;
    private Date transactionDate;
    private String dlNo;
    private String driverName;
    private Double supplyConsignmentWeight;
    private String poNo;
    private String tpNo;
    private String challanNo;
    private String ewaybillNo;
}
