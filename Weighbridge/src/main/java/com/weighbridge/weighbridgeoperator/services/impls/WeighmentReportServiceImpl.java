package com.weighbridge.weighbridgeoperator.services.impls;





import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;

import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse2;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
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

    @Override
    public Map<String, Map<String, List<WeighmentReportResponse>>> generateWeighmentReport(LocalDate startDate, LocalDate endDate) {
        // Default to today's date if startDate is not provided
        if (startDate == null&&endDate==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Date should provided");
        }
        if (startDate == null) {
            startDate = endDate;
        }
        if(endDate==null){
            endDate=startDate;
        }
        LocalDate finalStartDate = startDate;
        LocalDate finalEndDate = endDate;
        return gateEntryTransactionService.getAllGateEntryTransaction().stream()
                .filter(response -> isValidTransaction(response, finalStartDate, finalEndDate))
                .map(this::mapToWeighmentReportResponse)
                .sorted(Comparator.comparing(WeighmentReportResponse::getTransactionDate)) // Sort by transaction date
                .collect(Collectors.groupingBy(WeighmentReportResponse::getMaterialName,
                        LinkedHashMap::new,
                        Collectors.groupingBy(WeighmentReportResponse::getSupplier)));
    }

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

    private WeighmentReportResponse mapToWeighmentReportResponse(GateEntryTransactionResponse gateEntryResponse) {
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(gateEntryResponse.getTicketNo());

        WeighmentReportResponse weighmentReportResponse = new WeighmentReportResponse();
        weighmentReportResponse.setMaterialName(gateEntryResponse.getMaterial());
        String supplier = gateEntryResponse.getTransactionType().equalsIgnoreCase("Inbound") ?
                gateEntryResponse.getSupplier() : gateEntryResponse.getCustomer();
        weighmentReportResponse.setSupplier(supplier != null ? supplier : " ");
        weighmentReportResponse.setTransactionDate(gateEntryResponse.getTransactionDate());
        weighmentReportResponse.setVehicleNo(gateEntryResponse.getVehicleNo());
        weighmentReportResponse.setTpNo(gateEntryResponse.getTpNo() != null ? gateEntryResponse.getTpNo() : "");
        weighmentReportResponse.setChallanDate(gateEntryResponse.getChallanDate() != null ? gateEntryResponse.getChallanDate() : null);
        if(gateEntryResponse.getTransactionType().equalsIgnoreCase("Inbound")){
            weighmentReportResponse.setSupplyConsignmentWeight(gateEntryResponse.getTpNetWeight());
            weighmentReportResponse.setWeighQuantity(weighmentTransaction.getNetWeight());
            weighmentReportResponse.setExcessQty(gateEntryResponse.getTpNetWeight() - weighmentTransaction.getNetWeight());
        }
        else{
            weighmentReportResponse.setSupplyConsignmentWeight(weighmentTransaction.getNetWeight());
            weighmentReportResponse.setWeighQuantity(weighmentTransaction.getNetWeight());
            weighmentReportResponse.setExcessQty(0.0);
        }

        return weighmentReportResponse;
    }
    //new
    public List<WeighbridgeReportResponse> generateWeighmentReportDemo(LocalDate startDate, LocalDate endDate) {
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

                    WeighbridgeReportResponse2 weighbridgeResponse2 = mapToWeighbridgeReportResponse2(response);
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

    private WeighbridgeReportResponse2 mapToWeighbridgeReportResponse2(GateEntryTransactionResponse response) {
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(response.getTicketNo());
        WeighbridgeReportResponse2 weighbridgeReportResponse2 = new WeighbridgeReportResponse2();
        weighbridgeReportResponse2.setTransactionDate(response.getTransactionDate());
        weighbridgeReportResponse2.setVehicleNo(response.getVehicleNo());
        weighbridgeReportResponse2.setTpNo(response.getTpNo());
        weighbridgeReportResponse2.setChallanDate(response.getChallanDate());

        if (weighmentTransaction != null) {
            double supplyConsignmentWeight = response.getTpNetWeight();
            weighbridgeReportResponse2.setSupplyConsignmentWeight(supplyConsignmentWeight);
            Double netWeight = (Double) weighmentTransaction.getNetWeight() != null ? weighmentTransaction.getNetWeight() : 0.0;
            weighbridgeReportResponse2.setWeighQuantity(netWeight);
            weighbridgeReportResponse2.setExcessQty(response.getTpNetWeight() - netWeight);
        } else {
            weighbridgeReportResponse2.setSupplyConsignmentWeight(0.0);
            weighbridgeReportResponse2.setWeighQuantity(0.0);
            weighbridgeReportResponse2.setExcessQty(0.0);
        }

        return weighbridgeReportResponse2;
    }

}
