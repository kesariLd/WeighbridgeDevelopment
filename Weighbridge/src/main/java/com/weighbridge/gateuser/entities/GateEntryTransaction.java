package com.weighbridge.gateuser.entities;

import com.weighbridge.admin.entities.*;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
public class GateEntryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketNo;

    private long supplierId;
    private long transporterId;
    private long materialId;
    private long vehicleId;
    private String siteId;
    private String companyId;

    private LocalDate transactionDate;
    private String dlNo;
    private String driverName;
    private Double supplyConsignmentWeight;
    private String poNo;
    private String tpNo;
    private String challanNo;
    private String ewaybillNo;
    private String transactionType;
   /* private LocalDateTime vehicleInTime;
    private LocalDateTime vehicleOutTime;// may be change,it's not the effective way to do---could be in another table*/

}
