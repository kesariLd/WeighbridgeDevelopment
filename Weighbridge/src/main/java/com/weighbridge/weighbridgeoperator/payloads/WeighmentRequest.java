package com.weighbridge.weighbridgeoperator.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
 public class WeighmentRequest {
    @NotBlank
    private double Weight;

    @NotBlank(message = "MachineId required")
    private String machineId;

    @NotBlank(message = "ticketNo required")
    private Integer ticketNo;

}
