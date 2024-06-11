package com.weighbridge.weighbridgeoperator.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VehicleTransactionStatus class to store the current status of vehicle ,
 * where it belongs, it does not store the history like Transaction Log
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleTransactionStatus {
    @Id
    private Integer ticketNo;
    private String statusCode;

}
