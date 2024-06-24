package com.weighbridge.weighbridgeoperator.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.weighbridgeoperator.payloads.*;
import com.weighbridge.weighbridgeoperator.services.WeighbridgeOperatorPrintService;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import com.weighbridge.weighbridgeoperator.services.impls.TicketImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/weighment")
public class WeighmentTransactionController {
    @Autowired
    private WeighmentTransactionService weighmentTransactionService;

    @Autowired
    private WeighbridgeOperatorPrintService weighbridgeOperatorPrintService;

    @PostMapping("/measure")
    public ResponseEntity<String> measureWeight(@RequestParam("weighmentRequest") String weighmentRequest,
                                                @RequestParam("userId") String userId,
                                                @RequestParam(value = "frontImg1",required = false) MultipartFile frontImg1,
                                                @RequestParam(value = "backImg2",required = false) MultipartFile backImg2,
                                                @RequestParam(value = "topImg3", required = false) MultipartFile topImg3,
                                                @RequestParam(value = "bottomImg4", required = false) MultipartFile bottomImg4,
                                                @RequestParam(value = "leftImg5", required = false) MultipartFile leftImg5,
                                                @RequestParam(value = "rightImg6", required = false) MultipartFile rightImg6,
                                                @RequestParam("role") String role, ObjectMapper objectMapper
                                                ){
        objectMapper.registerModule(new JavaTimeModule());

        WeighmentRequest weighmentRequest1;
        try {
            weighmentRequest1 = objectMapper.readValue(weighmentRequest, WeighmentRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid JSON format in requestBody");
        }

        // Call service method with parsed request and file parameters
        String str = weighmentTransactionService.saveWeight(
                weighmentRequest1, userId, frontImg1, backImg2, topImg3, bottomImg4, leftImg5, rightImg6, role);
//        String str = weighmentTransactionService.saveWeight(weighmentRequest,userId);
        return ResponseEntity.ok(str);
    }

    @GetMapping("/getAll")
    public ResponseEntity<WeighbridgePageResponse> getAlldetails( @RequestParam(defaultValue = "0", required = false) int page,
                                                                             @RequestParam(defaultValue = "5", required = false) int size,
                                                                             @RequestParam(required = false, defaultValue = "ticketNo") String sortField,
                                                                             @RequestParam(defaultValue = "desc", required = false) String sortOrder,@RequestParam String userId){
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }

      WeighbridgePageResponse response=weighmentTransactionService.getAllGateDetails(pageable,userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{ticketNo}")
    public ResponseEntity<TicketResponse> getResponseByTicket(@PathVariable Integer ticketNo){
        TicketResponse responseByTicket = weighmentTransactionService.getResponseByTicket(ticketNo);
        return ResponseEntity.ok(responseByTicket);
    }
    @GetMapping("/view/{ticketNo}")
    public ResponseEntity<TicketImageResponse> viewResponseByTicket(@PathVariable Integer ticketNo,@RequestParam String userId){
        TicketImageResponse responseByTicket = weighmentTransactionService.viewResponseByTicket(ticketNo,userId);
        return ResponseEntity.ok(responseByTicket);
    }
    @GetMapping("/getPrintTicketWise/{ticketNo}")
    public ResponseEntity<WeighbridgeOperatorPrint> getPrintResponse(@PathVariable Integer ticketNo){
        WeighbridgeOperatorPrint printResponse = weighbridgeOperatorPrintService.getPrintResponse(ticketNo);
        return ResponseEntity.ok(printResponse);
    }

    @GetMapping("/getCompletedTransaction")
    public ResponseEntity<WeighbridgePageResponse> getCompletedTransactions(@RequestParam(defaultValue = "0", required = false) int page,
                                                                            @RequestParam(defaultValue = "5", required = false) int size,
                                                                            @RequestParam(required = false, defaultValue = "gateEntryTransaction") String sortField,
                                                                            @RequestParam(defaultValue = "desc", required = false) String sortOrder,@RequestParam String userId){
        Pageable pageable;
        if(sortField!=null && !sortField.isEmpty()){
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc")?Sort.Direction.DESC:Sort.Direction.ASC;
            Sort sort = Sort.by(direction,sortField);
            pageable = PageRequest.of(page,size,sort);
        }
        else{
            pageable = PageRequest.of(page,size);
        }
        WeighbridgePageResponse allCompletedTickets = weighmentTransactionService.getAllCompletedTickets(pageable,userId);
        return ResponseEntity.ok(allCompletedTickets);
    }
}