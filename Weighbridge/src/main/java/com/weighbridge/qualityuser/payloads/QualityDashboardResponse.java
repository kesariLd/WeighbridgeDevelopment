package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class represents the response structure for the quality dashboard.
 */
@Data
public class QualityDashboardResponse {

    /**
     * Ticket number associated with the transaction..
     */
    private Integer ticketNo;
    /**
     * Transaction date when the transaction was created.
     */
    private LocalDate date;

    /**
     * Vehicle number.
     */
    private String vehicleNo;

    /**
     * Timestamp when the vehicle entered the gate.
     */
    private String in;

    /**
     * Timestamp when the vehicle exit the gate.
     */
    private String out;

    /**
     * Transporter name of the vehicle.
     */
    private String transporterName;

    /**
     * Material to be checked for quality.
     */
    private String materialName;

    /**
     * Material type of the material.
     */
    private String materialType;

    /**
     * Transit Pass number.
     */
    private String tpNo;

    /**
     * Purchase order number.
     */
    private String poNo;

    /**
     * Challan number.
     */
    private String challanNo;

    /**
     * Supplier or customer name.
     */
    private String supplierOrCustomerName;

    /**
     * Supplier or customer address.
     */
    private String supplierOrCustomerAddress;

    /**
     * Transaction type. (Inbound or Outbound)
     */
    private String transactionType;

    private Boolean qualityParametersPresent;
    private Boolean isQualityGood;
}
