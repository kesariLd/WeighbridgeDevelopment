package com.weighbridge.admin.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "material_type_master")
public class MaterialTypeMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long materialTypeId;
    private String materialTypeName;

    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "material_id")
    private MaterialMaster materialMaster;

}
