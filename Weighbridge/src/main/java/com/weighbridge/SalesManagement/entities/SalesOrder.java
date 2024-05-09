package com.weighbridge.SalesManagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class SalesOrder {
    @Id
    private String purchaseOrderNo;

    @NotNull
    private Date purchaseOrderedDate;

    @NotNull
    private String saleOrderNo;

    @NotNull
    private String customerName;

    private String customerAddress;

    private String customerContact;

    private String customerEmail;

    @NotNull
    private String productName;

    private double orderedQuantity;

    private double progressiveQuantity;

    private double balanceQuantity;

    private String brokerName;

    private String brokerAddress;
}