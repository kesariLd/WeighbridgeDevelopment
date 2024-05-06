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

    private String moisture;
    private String Vm;
    private String Ash;
    private String FC;
    private String size_20mm;
    private String size_03mm;
    private String fe_t;
    private String Loi;


    @OneToOne
    @JoinColumn(name="ticket_no",referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;

}
