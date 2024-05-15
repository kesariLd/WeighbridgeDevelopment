package com.weighbridge.weighbridgeoperator.services.impls;





import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;

import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse2;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeighmentReportServiceImpl implements WeighmentReportService {

    @Autowired
    private GateEntryTransactionService gateEntryTransactionService;

    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    private boolean isValidTransaction(GateEntryTransactionResponse response, LocalDate startDate, LocalDate endDate) {
        LocalDate transactionDate = response.getTransactionDate();

        // Check if the transaction is within the specified date range
        boolean isWithinDateRange = !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);

        // Check if the transaction is valid based on the status code and transaction type
        boolean isValidTransaction = false;
        if (response.getTransactionType().equalsIgnoreCase("Inbound")) {
            isValidTransaction = transactionLogRepository.existsByTicketNoAndStatusCode(response.getTicketNo(), "TWT");
        } else if (response.getTransactionType().equalsIgnoreCase("Outbound")) {
            isValidTransaction = transactionLogRepository.existsByTicketNoAndStatusCode(response.getTicketNo(), "GWT");
        }

        // Return true only if the transaction is within the date range and is valid based on the status code
        return isWithinDateRange && isValidTransaction;
    }
    //new
    public List<WeighbridgeReportResponse> generateWeighmentReport(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is not provided");
        }
        if (startDate == null && endDate != null) {
            startDate = endDate;
        }
        if (startDate != null && endDate == null) {
            endDate = startDate;
        }
        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        List<GateEntryTransactionResponse> gateEntryTransactionResponses = gateEntryTransactionService.getAllGateEntryTransaction();

        Map<String, Map<String, List<WeighbridgeReportResponse2>>> groupedReports = new HashMap<>();

        gateEntryTransactionResponses.stream()
                .filter(response -> {
                    LocalDate transactionDate = response.getTransactionDate();
                    return transactionDate != null && !transactionDate.isBefore(finalStartDate) && !transactionDate.isAfter(finalEndDate);
                })
                .filter(response -> isValidTransaction(response, finalStartDate, finalEndDate))
                .forEach(response -> {
                    String materialName = response.getMaterial();
                    String supplierOrCustomer = response.getTransactionType().equalsIgnoreCase("Inbound") ?
                            response.getSupplier() : response.getCustomer();
                    String key = materialName + "-" + supplierOrCustomer;

                    WeighbridgeReportResponse2 weighbridgeResponse2 = mapToWeighbridgeReportResponse(response);
                    groupedReports.computeIfAbsent(key, k -> new HashMap<>())
                            .computeIfAbsent(supplierOrCustomer, k -> new ArrayList<>())
                            .add(weighbridgeResponse2);
                });

        return groupedReports.entrySet().stream()
                .map(entry -> {
                    WeighbridgeReportResponse report = new WeighbridgeReportResponse();
                    String[] keys = entry.getKey().split("-");
                    report.setMaterialName(keys[0]);
                    report.setSupplierOrCustomer(keys[1]);
                    report.setWeighbridgeResponse2List(new ArrayList<>(entry.getValue().values()).get(0));
                    return report;
                })
                .collect(Collectors.toList());
    }

    private WeighbridgeReportResponse2 mapToWeighbridgeReportResponse(GateEntryTransactionResponse gateEntryResponse) {
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(gateEntryResponse.getTicketNo());
        WeighbridgeReportResponse2 weighbridgeReportResponse2 = new WeighbridgeReportResponse2();
        weighbridgeReportResponse2.setTransactionDate(gateEntryResponse.getTransactionDate());
        weighbridgeReportResponse2.setVehicleNo(gateEntryResponse.getVehicleNo());
        weighbridgeReportResponse2.setTpNo(gateEntryResponse.getTpNo());
        weighbridgeReportResponse2.setChallanDate(gateEntryResponse.getChallanDate());

        if (weighmentTransaction != null) {
            double supplyConsignmentWeight = gateEntryResponse.getTpNetWeight();
            weighbridgeReportResponse2.setSupplyConsignmentWeight(supplyConsignmentWeight);
            Double netWeight = (Double) weighmentTransaction.getNetWeight() != null ? weighmentTransaction.getNetWeight() : 0.0;
            weighbridgeReportResponse2.setWeighQuantity(netWeight);
            weighbridgeReportResponse2.setExcessQty(supplyConsignmentWeight - netWeight);
        } else {
            weighbridgeReportResponse2.setSupplyConsignmentWeight(0.0);
            weighbridgeReportResponse2.setWeighQuantity(0.0);
            weighbridgeReportResponse2.setExcessQty(0.0);
        }

        return weighbridgeReportResponse2;
    }

}
