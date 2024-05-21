package com.weighbridge.SalesManagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
public class SalesProcess {
    @Id
    private String salePassNo;
    @ManyToOne
    @JoinColumn(name = "sale_order_no")
    private SalesOrder purchaseSale;
    private String productName;
    private String productType;
    private String vehicleNo;
    private String transporterName;
    private double consignmentWeight;
    private LocalDate purchaseProcessDate;

    // //to remove record from dashboard on basis of status
    private boolean status=true;
}