package com.weighbridge.weighbridgeoperator.payloads;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class WeighmentTransactionResponse {

        private String ticketNo;
        private String weighmentNo;
        private String transactionType;
        private Date transactionDate;
        private String grossWeight;
        private String tareWeight;
        private String netWeight;
        private String vehicleNo;
        private Date vehicleFitnessUpTo;
        private String supplierName;
        private String transporterName;
        private String materialName;
}
