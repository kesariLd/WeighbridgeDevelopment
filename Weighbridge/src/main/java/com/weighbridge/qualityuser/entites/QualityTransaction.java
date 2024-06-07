package com.weighbridge.qualityuser.entites;

import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * This class represents a Quality Transaction entity.
 * It holds the quality parameters and is associated with a Gate Entry Transaction through a OneToOne Transaction through a OneToOne relationship.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "quality_transaction")
public class QualityTransaction {

    /**
     * The unique identifier for the Quality Transaction entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    private String qualityRangeId;
    private String qualityValues;
    private Boolean isQualityGood;
    /**
     * The associated Gate Entry Transaction.
     *
     * @see GateEntryTransaction
     */
    @OneToOne
    @JoinColumn(name = "ticket_no", referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;

}