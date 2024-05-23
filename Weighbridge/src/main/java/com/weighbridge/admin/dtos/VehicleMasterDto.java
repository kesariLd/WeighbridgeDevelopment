package com.weighbridge.admin.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VehicleMasterDto {
    private String vehicleNo;
    private String vehicleType;
    private String vehicleManufacturer;
    private Integer vehicleWheelsNo;
    private Double vehicleTareWeight;
    private Double vehicleLoadCapacity;
    private LocalDate vehicleFitnessUpTo;
    private String status;

}