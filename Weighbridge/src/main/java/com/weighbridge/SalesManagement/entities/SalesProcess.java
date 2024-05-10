package com.weighbridge.SalesManagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class SalesProcess {
    @Id
    private String purchasePassNo;
    @ManyToOne
    @JoinColumn(name = "purchase_order_no")
    private SalesOrder purchaseSale;
    private String productName;
    private String productType;
    private String vehicleNo;
    private String transporterName;
    private double netWeight;
    private Date purchaseProcessDate;
}