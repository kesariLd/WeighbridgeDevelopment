package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

/**
 * This class represents the response payload for printing weighment transaction details.
 * It contains various attributes related to the weighment process.
 */
@Data
public class WeighmentPrintResponse {

    /**
     * The company name where transaction is occurred.
     */
    private String companyName;

    /**
     * The address of the company.
     */
    private String companyAddress;

    /**
     * The ticket number for the weighment transaction.
     */
    private Integer ticketNo;

    /**
     * The vehicle number associated with the transaction.
     */
    private String vehicleNo;

    /**
     * The name of the material being weighed.
     */
    private String materialName;

    /**
     * The name of the transporter of the vehicle.
     */
    private String transporterName;

    /**
     * The name of the supplier or customer associated with the weighment.
     */
    private String supplierOrCustomerName;

    /**
     * The challan number related to the weighment.
     */
    private String challanNo;

    /**
     * The gross weight measured during the weighment.
     */
    private Double grossWeight;

    /**
     * The date and time when the gross weight was measured.
     */
    private String grossWeightDateTime;

    /**
     * The tare weight measured during the weighment.
     */
    private Double tareWeight;

    /**
     * The date and time when the tare weight was measured.
     */
    private String tareWeightDateTime;

    /**
     * The net weight calculated by subtracting the tare weight from the gross weight.
     */
    private Double netWeight;

    /**
     * The name of the operator who performed the weighment.
     */
    private String operatorName;
}
