package com.weighbridge.admin.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userId;

    private String roles;

    private String Company;
    private String site;
    @Column(name = "user_created_by")
    private String userCreatedBy;

    @Column(name = "user_created_date")
    private LocalDateTime userCreatedDate;

    @Column(name = "user_modified_by")
    private String userModifiedBy;

    @Column(name = "user_modified_date")
    private LocalDateTime userModifiedDate;



}
