package com.weighbridge.admin.payloads;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Data
public class VehicleRequest {
    private String vehicleNo;
    private String vehicleType;
    private String vehicleManufacturer;
    private Integer vehicleWheelsNo;
    private Double vehicleTareWeight;
    private Double vehicleLoadCapacity;
    private LocalDate vehicleFitnessUpTo;
}