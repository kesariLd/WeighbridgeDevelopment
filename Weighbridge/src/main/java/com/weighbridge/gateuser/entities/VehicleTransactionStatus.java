package com.weighbridge.gateuser.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * VehicleTransactionStatus class to store the current status of vehicle ,
 * where it belongs, it does not store the history like Transaction Log
 */
@Entity
@Data
public class VehicleTransactionStatus {
    @Id
    private Integer ticketNo;

    private String statusCode;
}
