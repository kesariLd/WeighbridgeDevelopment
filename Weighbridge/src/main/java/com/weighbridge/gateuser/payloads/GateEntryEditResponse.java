package com.weighbridge.gateuser.payloads;

import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class GateEntryEditResponse {

        private String ticketNo;
        private String supplier;
        private String supplierAddressLine1;
        private String customer;
        private String customerAddressLine;
        private String transporter;
        private String material;
        private String materialType;
        private String vehicle;
        private String vehicleType;
        private Integer vehicleWheelsNo;
        private LocalDate vehicleFitnessUpTo;
        private String site;
        private String transactionType;
        private String company;
        private String dlNo;
        private String driverName;
        private Double supplyConsignmentWeight;
        private String poNo;
        private String tpNo;
        private String challanNo;
        private String ewayBillNo;
        private LocalDate challanDate;
        private Map<String, byte[]> imagesMap ;
    }

