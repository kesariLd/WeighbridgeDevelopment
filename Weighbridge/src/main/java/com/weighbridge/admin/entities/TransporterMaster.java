package com.weighbridge.admin.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class
TransporterMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String transporterName;
    private String transporterContactNo;
    private String transporterEmailId;
    private String transporterAddress;
    private String status = "ACTIVE";
    private String transporterCreatedBy;
    private LocalDateTime transporterCreatedDate;
    private String transporterModifiedBy;
    private LocalDateTime transporterModifiedDate;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "transporter_vehicle",
            joinColumns = {@JoinColumn(name = "transporter_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "vehicle_Id", referencedColumnName = "id")})
    private Set<VehicleMaster> vehicles = new HashSet<>();

    // Add existing vehicle to existing transporter
    public void addVehicle(VehicleMaster vehicleMaster) {
        this.vehicles.add(vehicleMaster);
        vehicleMaster.getTransporter().add(this);
    }

}