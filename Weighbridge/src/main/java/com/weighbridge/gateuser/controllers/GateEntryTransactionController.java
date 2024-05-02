package com.weighbridge.gateuser.controllers;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for GateEntryTransactionController
 */
@RestController
@RequestMapping("/api/v1/gate")
public class GateEntryTransactionController {

    @Autowired
    private GateEntryTransactionService gateEntryTransactionService;

    /**
     * Save a gate entry transaction.
     *
     * @param gateEntryTransactionRequest The gate entry transaction details to be saved.
     * @return The ID of the saved gate entry transaction.
     */
    @PostMapping
    public ResponseEntity<GateEntryTransaction> saveTransaction(@RequestBody GateEntryTransactionRequest gateEntryTransactionRequest) {
        GateEntryTransaction gateEntryResponse = gateEntryTransactionService.saveGateEntryTransaction(gateEntryTransactionRequest);
        return new ResponseEntity<>(gateEntryResponse, HttpStatus.OK);
    }

    /**
     * Get all gate entry transactions.
     *
     * @return A list of all gate entry transactions.
     */
    @GetMapping
    public ResponseEntity<List<GateEntryTransactionResponse>> getAllTransaction() {
        List<GateEntryTransactionResponse> allGateEntryTransaction = gateEntryTransactionService.getAllGateEntryTransaction();
        return new ResponseEntity<>(allGateEntryTransaction, HttpStatus.OK);
    }

    /**
     * Save the out time for a gate entry transaction.
     *
     * @param ticketNo The ticket number of the gate entry transaction.
     * @return The status of the operation.
     */
    @PostMapping("/out/{ticketNo}")
    public ResponseEntity<String> saveOutTime(@PathVariable Integer ticketNo) {
        String status = gateEntryTransactionService.setOutTime(ticketNo);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
