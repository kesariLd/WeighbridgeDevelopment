package com.weighbridge.admin.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VehicleMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "vehicle_no",nullable = false,unique = true)
    private String vehicleNo;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_manufacturer")
    private String vehicleManufacturer;

    @Column(name = "vehicle_wheels_no")
    private Integer vehicleWheelsNo;

    @Column(name = "vehicle_tare_weight")
    private Double vehicleTareWeight;

    @Column(name = "vehicle_load_capacity")
    private Double vehicleLoadCapacity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "vehicle_fitness_up_to")
    private LocalDate vehicleFitnessUpTo;

    @Column(name = "vehicle_status")
    private String vehicleStatus = "ACTIVE";

    @Column(name = "vehicle_created_by")
    private String vehicleCreatedBy;

    @Column(name = "vehicle_created_date")
    private LocalDateTime vehicleCreatedDate;

    @Column(name = "vehicle_modified_by")
    private String vehicleModifiedBy;

    @Column(name = "vehicle_modified_date")
    private LocalDateTime vehicleModifiedDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "vehicles")
    private Set<TransporterMaster> transporter = new HashSet<>();


}