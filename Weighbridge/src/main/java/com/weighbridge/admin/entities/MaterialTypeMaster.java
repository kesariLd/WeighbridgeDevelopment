package com.weighbridge.admin.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "material_type")
public class MaterialTypeMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long materialTypeId;
    private String materialTypeName;

    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "material_id")
    private MaterialMaster materialMaster;

    @OneToMany(mappedBy = "materialTypeMaster")
    private List<QualityRange> qualityRanges;


}
