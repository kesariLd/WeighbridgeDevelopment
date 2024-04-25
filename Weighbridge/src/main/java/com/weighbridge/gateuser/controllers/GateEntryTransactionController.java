package com.weighbridge.gateuser.controllers;

import com.weighbridge.admin.entities.CompanyMaster;
import com.weighbridge.gateuser.dtos.GateEntryTransactionDto;

import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/v1/gate")
@RestController
public class GateEntryTransactionController{

    @Autowired
    GateEntryTransactionService gateEntryTransactionService;

    @PostMapping
    public ResponseEntity<GateEntryTransaction> saveTransaction(@RequestBody GateEntryTransactionRequest gateEntryTransactionRequest){
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionService.saveGateEntryTransaction(gateEntryTransactionRequest);
        return new ResponseEntity<>(gateEntryTransaction, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<GateEntryTransactionResponse>> getAllTransaction(){
        List<GateEntryTransactionResponse> allGateEntryTraansaction = gateEntryTransactionService.getAllGateEntryTransaction();
        return new ResponseEntity<>(allGateEntryTraansaction,HttpStatus.OK);
    }
    @PostMapping("/out/{ticketNo}")
    public ResponseEntity<String> saveOutTime(@PathVariable Integer ticketNo){
        String status = gateEntryTransactionService.setOutTime(ticketNo);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }

}
