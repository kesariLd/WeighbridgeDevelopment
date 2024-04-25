package com.weighbridge.gateuser.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // New ID field

    private Integer ticketNo;

    private String statusCode;

    private LocalDateTime timestamp;

    private String userId;
}
