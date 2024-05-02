package com.weighbridge.admin.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "range_from", scale = 3)
    private Double rangeFrom;

    @Column(name = "range_to", scale = 3)
    private Double rangeTo;

}

