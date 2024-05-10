package com.weighbridge.admin.payloads;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weighbridge.admin.entities.TransporterMaster;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Data
public class VehicleGateEntryResponse {
    private String vehicleNo;
    private Integer vehicleWheelsNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate vehicleFitnessUpTo;
    private List<String> transporter;
    private String vehicleType;
}
