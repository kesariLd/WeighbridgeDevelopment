package com.weighbridge.admin.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "sequence_generator")
@IdClass(SequenceGeneratorPK.class)
@AllArgsConstructor
@NoArgsConstructor
public class SequenceGenerator {
    @Id
    @Column(name = "company_id")
    private String companyId;

    @Id
    @Column(name = "site_id")
    private String siteId;

    @Column(name = "next_value")
    private int nextValue;



}

