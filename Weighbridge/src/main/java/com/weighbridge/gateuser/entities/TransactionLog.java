package com.weighbridge.gateuser.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * TransactionLog class is used for store the history or log of all transactions per ticket no. such as GNT,GXT,TWT,GWT
 */
@Entity
@Data
@Table(name = "transaction_log", indexes = {
        @Index(name = "idx_transaction_log", columnList = "ticketNo, statusCode")
})
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // New ID field

    //ticket no store the transaction ticket no.
    private Integer ticketNo;
    //status code store current ticket no. vehicle status: GNT,GXT,TWT,GWT
    private String statusCode;

    private LocalDateTime timestamp;
    //who changed the current transaction , that userId
    private String userId;
}
