package com.weighbridge.SalsesManagement.entities;

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
    @JoinColumn(name = "purchaseOrderNo",referencedColumnName = "purchaseOrderNo")
    private SalesOrder purchaseOrderNo;
    private String productType;
    private String vehicleNo;
    private String TransporterName;
    private double netWeight;
    private Date purchaseProcessDate;
}