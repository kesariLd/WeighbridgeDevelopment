package com.weighbridge.gateuser.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class VehicleTransactionStatus {
    @Id
    private Integer ticketNo;

    private String statusCode;
}
