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
    private long qualityId;

    /**
     * Moisture content of the product.
     */
    private Double moisture;

    /**
     * Viscosity measurement fo the product
     */
    private Double vm;

    /**
     * Ash content of the product.
     */
    private Double ash;

    /**
     * Fixed carbon content of the product.
     */
    private Double fc;

    /**
     * Size of the product in 20mm category.
     */
    private Double size_20mm;

    /**
     * Size of the product in 0.3mm category.
     */
    private Double size_03mm;

    /**
     * Iron content of the product.
     */
    private Double fe_t;

    /**
     * Loss of Ignition of the product.
     */
    private Double loi;

    /**
     * The associated Gate Entry Transaction.
     *
     * @see GateEntryTransaction
     */
    @OneToOne
    @JoinColumn(name = "ticket_no", referencedColumnName = "ticketNo")
    private GateEntryTransaction gateEntryTransaction;
}
