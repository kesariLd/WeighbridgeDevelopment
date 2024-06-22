package com.weighbridge.admin.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camera_master")
public class CameraMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyId;
    private String siteId;
    private Integer roleId;
    private String camUrl1;
    private String camUrl2;
    private String camUrl3;
    private String camUrl4;
}
