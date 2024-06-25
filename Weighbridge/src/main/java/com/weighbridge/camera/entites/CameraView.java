package com.weighbridge.camera.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "camera_view")
public class CameraView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer ticketNo;
    private String frontImg1;
    private String backImg2;
    private String topImg3;
    private String bottomImg4;
    private String leftImg5;
    private String rightImg6;
    private LocalDate date;
    private Integer roleId;
    private String truckStatus;
}
