package com.weighbridge.qualityuser.payloads;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents the response structure for the quality details.
 */
@Data
public class QualityCreationResponse {
    /**
     * Ticket number.
     */
    private Integer ticketNo;

    /**
     * Transaction date.
     */
    private LocalDate transactionDate;

    /**
     * Vehicle number.
     */
    private String vehicleNo;

    /**
     * Vehicle in time.
     */
    private LocalDateTime vehicleInTime;

    /**
     * Vehicle out time.
     */
    private LocalDateTime vehicleOutTime;

    /**
     * Transporter name.
     */
    private String transporterName;

    /**
     * Material name.
     */
    private String materialName;

    /**
     * Material type name.
     */
    private String materialTypeName;

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
     * Supplier or customer number.
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

    /**
     * List of parameters.
     */
    private List<Parameter> parameters;

    /**
     * This class represents parameters for the transaction quality details.
     */
    @Data
    public static class Parameter {

        /**
         * Parameter name.
         */
        private String parameterName;

        /**
         * Returns from.
         */
        private Double rangeFrom;

        /**
         * Returns to.
         */
        private Double rangeTo;

        /**
         * Parameter value.
         */
        private Double parameterValue;
    }
}
