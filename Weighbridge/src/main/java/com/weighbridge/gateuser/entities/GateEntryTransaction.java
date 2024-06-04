package com.weighbridge.gateuser.entities;

import com.weighbridge.admin.entities.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@ToString
@Table(name = "gate_entry_transaction", indexes = {
        @Index(name = "idx_gate_entry_transaction", columnList = "siteId, companyId, transactionDate DESC")
})
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
    @Column(unique = true)
    private String tpNo;
    @Column(unique = true)
    private String challanNo;
    private String ewaybillNo;
    private String transactionType;
    private LocalDate challanDate;

}