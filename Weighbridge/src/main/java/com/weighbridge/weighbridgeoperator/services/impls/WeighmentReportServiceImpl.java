package com.weighbridge.weighbridgeoperator.services.impls;





import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;

import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
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

}
