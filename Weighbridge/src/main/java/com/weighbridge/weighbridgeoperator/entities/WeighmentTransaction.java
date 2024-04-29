package com.weighbridge.weighbridgeoperator.entities;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeighmentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer weighmentNo;
    private double grossWeight;
    private double tareWeight;
    private double netWeight;
    private String machineId;

    private double temporaryWeight;

    @OneToOne
    @JoinColumn(name="ticketNo",referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;
}
