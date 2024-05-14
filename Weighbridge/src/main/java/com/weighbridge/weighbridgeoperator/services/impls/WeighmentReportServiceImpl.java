package com.weighbridge.weighbridgeoperator.services.impls;





import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;

import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        LocalDate finalStartDate = startDate;
        return gateEntryTransactionService.getAllGateEntryTransaction().stream()
                .filter(response -> isValidTransaction(response, finalStartDate, endDate))
                .map(this::mapToWeighmentReportResponse)
                .collect(Collectors.groupingBy(WeighmentReportResponse::getMaterialName,
                        Collectors.groupingBy(WeighmentReportResponse::getSupplier)));
    }

    private boolean isValidTransaction(GateEntryTransactionResponse response, LocalDate startDate, LocalDate endDate) {
        LocalDate transactionDate = response.getTransactionDate();
        // Check if the transaction date falls within the specified date range
        // Check if endDate is null and use today's date if it is
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate) &&
                // Check if the transaction is valid based on the status code
                (response.getTransactionType().equalsIgnoreCase("Inbound") &&
                        transactionLogRepository.existsByTicketNoAndStatusCode(response.getTicketNo(), "TWT")) ||
                (response.getTransactionType().equalsIgnoreCase("Outbound") &&
                        transactionLogRepository.existsByTicketNoAndStatusCode(response.getTicketNo(), "GWT"));
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
        weighmentReportResponse.setChallanDate(gateEntryResponse.getChallanDate());
        weighmentReportResponse.setSupplyConsignmentWeight(gateEntryResponse.getTpNetWeight());
        weighmentReportResponse.setWeighQuantity(weighmentTransaction.getNetWeight());
        weighmentReportResponse.setTotalQuantity(gateEntryResponse.getTpNetWeight() - weighmentTransaction.getNetWeight());

        return weighmentReportResponse;
    }

}
