package com.weighbridge.admin.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "quality_range_master")
public class QualityRangeMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quality_range_id")
    private Long qualityRangeId;
    private String parameterName;

    @Column(name = "range_from", scale = 3)
    private Double rangeFrom;

    @Column(name = "range_to", scale = 3)
    private Double rangeTo;

    private String supplierName;
    private String supplierAddress;

    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "material_id")
    private MaterialMaster materialMaster;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private ProductMaster productMaster;
}

