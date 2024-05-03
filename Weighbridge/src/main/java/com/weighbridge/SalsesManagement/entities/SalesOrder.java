package com.weighbridge.SalsesManagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/*@Entity
@Data*/
public class SalesOrder {
    //@Id
    private String purchaseOrderNo;

    @NotBlank
    private Date purchaseOrderedDate;

    @NotBlank
    private String saleOrderNo;


    private String customerName;
    private String customerAddress;


    private String productName;


    private double orderedQuantity;


    private double progressiveQuantity;


    private double balanceQuantity;

    @NotBlank
    private String brokerName;
}
