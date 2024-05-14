package com.weighbridge.weighbridgeoperator.services.impls;




import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentReportResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.xhtmlrenderer.pdf.ITextRenderer;

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
    public Map<String, List<WeighmentReportResponse>> generateWeighmentReport() {

        List<GateEntryTransactionResponse> gateEntryTransactionResponseList = gateEntryTransactionService.getAllGateEntryTransaction();
        List<WeighmentReportResponse> weighmentReportResponses = new ArrayList<>();

        for (GateEntryTransactionResponse gateEntryResponse : gateEntryTransactionResponseList) {
            if (transactionLogRepository.existsByTicketNoAndStatusCode(gateEntryResponse.getTicketNo(), "TWT")) {
                WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(gateEntryResponse.getTicketNo());
                WeighmentReportResponse weighmentReportResponse = new WeighmentReportResponse();

                weighmentReportResponse.setMaterialName(gateEntryResponse.getMaterial());
                weighmentReportResponse.setSupplier(gateEntryResponse.getSupplier());
                weighmentReportResponse.setTransactionDate(gateEntryResponse.getTransactionDate());
                weighmentReportResponse.setVehicleNo(gateEntryResponse.getVehicleNo());
                weighmentReportResponse.setTpNo(gateEntryResponse.getTpNo());

                LocalDate challanDate = gateEntryResponse.getChallanDate();
                weighmentReportResponse.setChallanDate(challanDate != null ? challanDate : null);

                weighmentReportResponse.setSupplyConsignmentWeight(gateEntryResponse.getTpNetWeight());
                weighmentReportResponse.setWeighQuantity(weighmentTransaction.getNetWeight());
                Double excessQuantity = gateEntryResponse.getTpNetWeight() - weighmentTransaction.getNetWeight();
                weighmentReportResponse.setTotalQuantity(excessQuantity);

                weighmentReportResponses.add(weighmentReportResponse);
            }
        }

        // Grouping WeighmentReportResponse objects by supplier
        Map<String, List<WeighmentReportResponse>> groupedBySupplier = weighmentReportResponses.stream()
                .collect(Collectors.groupingBy(WeighmentReportResponse::getSupplier));

        return groupedBySupplier;
    }

}
