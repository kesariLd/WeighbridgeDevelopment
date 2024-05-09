package com.weighbridge.qualityuser.entites;


import com.weighbridge.gateuser.entities.GateEntryTransaction;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quality_transaction")
public class QualityTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long qualityId;

    @Column(nullable = false)
    private Double moisture;

    @Column(nullable = false)
    private Double vm;

    @Column(nullable = false)
    private Double ash;

    @Column(nullable = false)
    private Double fc;

    @Column(nullable = false)
    private Double size_20mm;

    @Column(nullable = false)
    private Double size_03mm;

    @Column(nullable = false)
    private Double fe_t;

    @Column(nullable = false)
    private Double loi;

    @OneToOne
    @JoinColumn(name="ticket_no",referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;

}
