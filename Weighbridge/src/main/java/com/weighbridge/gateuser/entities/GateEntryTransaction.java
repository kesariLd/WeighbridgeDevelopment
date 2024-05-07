package com.weighbridge.gateuser.entities;

import com.weighbridge.admin.entities.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * GateEntryTransaction class for entity where gateEntry transaction data is stored
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GateEntryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketNo;

    private long supplierId;
    private long transporterId;
    private long materialId;
    private String materialType;
    private long vehicleId;
    private long customerId;
    private String siteId;
    private String companyId;
    private LocalDateTime vehicleIn;
    private LocalDateTime vehicleOut;
    private LocalDate transactionDate;
    private String dlNo;
    private String driverName;
    private Double supplyConsignmentWeight;
    private String poNo;
    private String tpNo;
    private String challanNo;
    private String ewaybillNo;
    private String transactionType;


}
