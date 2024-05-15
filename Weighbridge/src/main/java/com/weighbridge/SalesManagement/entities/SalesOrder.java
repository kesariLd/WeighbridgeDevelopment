package com.weighbridge.SalesManagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class SalesOrder {
    @Id
    private String saleOrderNo;

    @NotNull
    private LocalDate purchaseOrderedDate;

    @NotNull
    private String purchaseOrderNo;

    @NotNull
    private long customerId;

    @NotNull
    private String productName;

    private double orderedQuantity;

    private double progressiveQuantity=0.0;

    private double balanceQuantity;

    private String brokerName;

    private String brokerAddress;
}