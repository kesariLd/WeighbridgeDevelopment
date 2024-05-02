package com.weighbridge.admin.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "acceptable_quality_range")
public class AcceptableQualityRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quality_range_id")
    private Long qualityRangeId;

    @ManyToOne
    @JoinColumn(name = "parameter_id")
    private ParameterMaster parameter;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private MaterialMaster material;

    @Column(name = "range_from")
    private Double rangeFrom;

    @Column(name = "range_to")
    private Double rangeTo;

}

