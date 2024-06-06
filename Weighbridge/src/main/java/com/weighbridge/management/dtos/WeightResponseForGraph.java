package com.weighbridge.management.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeightResponseForGraph {
    private String transactionDate;
    private String MaterialName;
    private double totalQuantity;
}
