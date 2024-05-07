package com.weighbridge.qualityuser.entites;


import com.weighbridge.gateuser.entities.GateEntryTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quality_transaction")
public class QualityTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long qualityId;
    private double moisture;
    private double vm;
    private double ash;
    private double fc;
    private double size_20mm;
    private double size_03mm;
    private double fe_t;
    private double loi;


    @OneToOne
    @JoinColumn(name="ticket_no",referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;

}
