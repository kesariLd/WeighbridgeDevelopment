package com.weighbridge.gateuser.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.weighbridge.gateuser.dtos.GateEntryPrint;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.payloads.GateEntryEditResponse;
import com.weighbridge.gateuser.payloads.GateEntryTransactionPageResponse;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
     *
     * @return The ID of the saved gate entry transaction.
     */
    @PostMapping("/saveTransaction")
    public ResponseEntity<Integer> saveTransaction(
            @RequestParam("requestBody") String requestBody,
            @RequestParam("userId") String userId,
            @RequestParam(value = "frontImg1",required = false) MultipartFile frontImg1,
            @RequestParam(value = "backImg2",required = false) MultipartFile backImg2,
            @RequestParam(value = "topImg3", required = false) MultipartFile topImg3,
            @RequestParam(value = "bottomImg4", required = false) MultipartFile bottomImg4,
            @RequestParam(value = "leftImg5", required = false) MultipartFile leftImg5,
            @RequestParam(value = "rightImg6", required = false) MultipartFile rightImg6,
            @RequestParam("role") String role,ObjectMapper objectMapper) {

        // Configure ObjectMapper with JavaTimeModule
        objectMapper.registerModule(new JavaTimeModule());

        GateEntryTransactionRequest gateEntryTransactionRequest;
        try {
            gateEntryTransactionRequest = objectMapper.readValue(requestBody, GateEntryTransactionRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid JSON format in requestBody");
        }

        // Call service method with parsed request and file parameters
        Integer gateEntryResponse = gateEntryTransactionService.saveGateEntryTransaction(
                gateEntryTransactionRequest, userId, frontImg1, backImg2, topImg3, bottomImg4, leftImg5, rightImg6, role);

        return new ResponseEntity<>(gateEntryResponse, HttpStatus.OK);
    }

    /**
     * Get all gate entry transactions.
     *
     * @return A list of all gate entry transactions.
     */
    @GetMapping
    public ResponseEntity<GateEntryTransactionPageResponse> getAllTransaction( @RequestParam(defaultValue = "0", required = false) int page,
                                                                                 @RequestParam(defaultValue = "5", required = false) int size,
                                                                                 @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
                                                                               @RequestParam(defaultValue = "desc", required = false) String sortOrder,
                                                                               @RequestParam String userId
    ) {
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        GateEntryTransactionPageResponse allGateEntryTransaction = gateEntryTransactionService.getAllGateEntryTransaction(pageable, userId);
        return new ResponseEntity<>(allGateEntryTransaction, HttpStatus.OK);
    }

    @GetMapping("/edit/{ticketNo}")
    public ResponseEntity<GateEntryEditResponse> editGateEntryDetail(@PathVariable("ticketNo") Integer ticketNo, @RequestParam String userId) {
        GateEntryEditResponse gateEntryEditResponse = gateEntryTransactionService.editGateEntryByTicketNo(ticketNo, userId);
        return new ResponseEntity<>(gateEntryEditResponse,HttpStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<Integer> updateGateEntryDetail(@RequestBody GateEntryTransactionRequest gateEntryTransactionRequest,
                                                         @RequestParam String userId) {
        Integer gateEntryTransactionResponse = gateEntryTransactionService.updateGateEntryByTicketNo(gateEntryTransactionRequest, Integer.parseInt(gateEntryTransactionRequest.getTicketNo()), userId);
        return new ResponseEntity<>(gateEntryTransactionResponse,HttpStatus.OK);
    }
    /**
     * Save the out time for a gate entry transaction.
     *
     * @param ticketNo The ticket number of the gate entry transaction.
     * @return The status of the operation.
     */
    @PostMapping("/out/{ticketNo}")
    public ResponseEntity<String> saveOutTime(@PathVariable Integer ticketNo, @RequestParam String userId) {
        String status = gateEntryTransactionService.setOutTime(ticketNo, userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping("/transactions/completed")
    public GateEntryTransactionPageResponse getTransactionsCompleted(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortOrder,
            @RequestParam(required = false , defaultValue = "completed") String vehicleStatus,
            @RequestParam(required = false) Integer ticketNo,
            @RequestParam String userId,
            @RequestParam(required = false) String vehicleNo,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) LocalDate date) {
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        GateEntryTransactionPageResponse transactionsByFiltering = gateEntryTransactionService.findTransactionsByFiltering(ticketNo, vehicleNo, date, supplierName, transactionType, pageable, vehicleStatus, userId);
        return transactionsByFiltering;
    }
    @GetMapping("/transactions/ongoing")
    public GateEntryTransactionPageResponse getTransactionsOngoing(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
            @RequestParam(defaultValue = "desc", required = false) String sortOrder,
            @RequestParam(required = false , defaultValue = "ongoing") String vehicleStatus,
            @RequestParam(required = false) Integer ticketNo,
            @RequestParam(required = false) String vehicleNo,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String supplierName,
            @RequestParam String userId,
            @RequestParam(required = false) LocalDate date) {
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        GateEntryTransactionPageResponse transactionsByFiltering = gateEntryTransactionService.findTransactionsByFiltering(ticketNo, vehicleNo, date, supplierName, transactionType, pageable, vehicleStatus, userId);
        return transactionsByFiltering;
    }

    @GetMapping("/completedDashboard")
    public ResponseEntity<GateEntryTransactionPageResponse> getAllCompletedTransactions(@RequestParam(defaultValue = "0", required = false) int page,
                                                                                        @RequestParam(defaultValue = "5", required = false) int size,
                                                                                        @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
                                                                                        @RequestParam(defaultValue = "desc", required = false) String sortOrder,
                                                                                        @RequestParam String userId) {
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        GateEntryTransactionPageResponse allGateEntryTransaction = gateEntryTransactionService.getAllCompletedGateEntry(pageable,userId);
        return new ResponseEntity<>(allGateEntryTransaction, HttpStatus.OK);
    }

    @GetMapping("/print/{ticketNo}")
    public ResponseEntity<GateEntryPrint> getPrintByTicket(@PathVariable Integer ticketNo){
        GateEntryPrint printTicketWise = gateEntryTransactionService.getPrintTicketWise(ticketNo);
        return ResponseEntity.ok(printTicketWise);
    }
}