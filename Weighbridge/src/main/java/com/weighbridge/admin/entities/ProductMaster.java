package com.weighbridge.admin.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_master")
public class ProductMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_status")
    private String productStatus = "ACTIVE";
    
    @Column(name = "product_created_by")
    private String productCreatedBy;

    @Column(name = "product_created_date")
    private LocalDateTime productCreatedDate;

    @Column(name = "product_modified_by")
    private String productModifiedBy;

    @Column(name = "product_modified_date")
    private LocalDateTime productModifiedDate;

    @OneToMany(mappedBy = "productMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductTypeMaster> productTypes;

    @OneToMany(mappedBy = "productMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QualityRangeMaster> qualityRanges;
}
