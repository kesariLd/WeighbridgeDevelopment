package com.weighbridge.admin.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class VehicleMasterDto {
    private String vehicleNo;
    private String vehicleType;
    private String vehicleManufacturer;
    private Integer vehicleWheelsNo;
    private Double vehicleTareWeight;
    private Double vehicleLoadCapacity;
    private Date vehicleFitnessUpTo;
    private String vehicleCreatedBy;
    private LocalDateTime vehicleCreatedDate;
    private String vehicleModifiedBy;
    private LocalDateTime vehicleModifiedDate;

}