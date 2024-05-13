package com.weighbridge.qualityuser.entites;


import com.weighbridge.gateuser.entities.GateEntryTransaction;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Double moisture;
    private Double vm;
    private Double ash;
    private Double fc;
    private Double size_20mm;
    private Double size_03mm;
    private Double fe_t;
    private Double loi;

    @OneToOne
    @JoinColumn(name="ticket_no",referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;

}
