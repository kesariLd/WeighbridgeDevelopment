package com.weighbridge.qualityuser.services.Impl;

import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityCreationResponse;
import com.weighbridge.qualityuser.payloads.QualityRequest;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.repository.QualityTransactioRepository;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class QualityTransactionServicesImpl implements QualityTransactionService {

    private final QualityTransactioRepository qualityTransactioRepository;
    private final GateEntryTransactionRepository gateEntryTransactionRepository;
    private final HttpServletRequest httpServletRequest;
    private final VehicleTransactionStatusRepository vehicleTransactionStatusRepository;
    private final SupplierMasterRepository supplierMasterRepository;
    private final CustomerMasterRepository customerMasterRepository;
    private final MaterialMasterRepository materialMasterRepository;
    private final TransporterMasterRepository transporterMasterRepository;
    private final VehicleMasterRepository vehicleMasterRepository;
    private final TransactionLogRepository transactionLogRepository;
    private final QualityRangeMasterRepository qualityRangeMasterRepository;
    private final CompanyMasterRepository companyMasterRepository;
    private final ProductMasterRepository productMasterRepository;

    public QualityTransactionServicesImpl(QualityTransactioRepository qualityTransactioRepository,
                                          GateEntryTransactionRepository gateEntryTransactionRepository,
                                          HttpServletRequest httpServletRequest,
                                          VehicleTransactionStatusRepository vehicleTransactionStatusRepository,
                                          SupplierMasterRepository supplierMasterRepository,
                                          CustomerMasterRepository customerMasterRepository,
                                          MaterialMasterRepository materialMasterRepository,
                                          TransporterMasterRepository transporterMasterRepository,
                                          VehicleMasterRepository vehicleMasterRepository,
                                          TransactionLogRepository transactionLogRepository,
                                          QualityRangeMasterRepository qualityRangeMasterRepository, CompanyMasterRepository companyMasterRepository, ProductMasterRepository productMasterRepository) {
        this.qualityTransactioRepository = qualityTransactioRepository;
        this.gateEntryTransactionRepository = gateEntryTransactionRepository;
        this.httpServletRequest = httpServletRequest;
        this.vehicleTransactionStatusRepository = vehicleTransactionStatusRepository;
        this.supplierMasterRepository = supplierMasterRepository;
        this.customerMasterRepository = customerMasterRepository;
        this.materialMasterRepository = materialMasterRepository;
        this.transporterMasterRepository = transporterMasterRepository;
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.transactionLogRepository = transactionLogRepository;
        this.qualityRangeMasterRepository = qualityRangeMasterRepository;
        this.companyMasterRepository = companyMasterRepository;
        this.productMasterRepository = productMasterRepository;
    }

    @Override
    public List<QualityDashboardResponse> getAllGateDetails() {

        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }

        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTicketNoDesc(userSite, userCompany);

//        List<QualityDashboardResponse> qualityDashboardResponses = allTransactions.stream()
//                .filter(transaction -> transaction.getTransactionType().equals("Inbound") || transaction.getTransactionType().equals("Outbound"))
//                .flatMap(transaction -> {
//                    VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
//                    if (transactionStatus != null && (transactionStatus.getStatusCode().equals("GWT") || transactionStatus.getStatusCode().equals("TWT"))) {
//                        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
//                        qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
//                        qualityDashboardResponse.setTpNo(transaction.getTpNo());
//                        qualityDashboardResponse.setPoNo(transaction.getPoNo());
//                        qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
//                        qualityDashboardResponse.setTransactionType(transaction.getTransactionType());
//
//                        SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(transaction.getSupplierId());
//                        if (supplierMaster != null) {
//                            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
//                            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());
//                        }
//
//                        if (transaction.getTransactionType().equals("Inbound")){
//                            MaterialMaster materialMaster = materialMasterRepository.findById(transaction.getMaterialId())
//                                    .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
//                            if (materialMaster != null) {
//                                qualityDashboardResponse.setMaterialName(materialMaster.getMaterialName());
//                            }
//                            qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
//                        }
//
//                        if ("Outbound".equals(transaction.getTransactionType())){
//                            log.info("MaterialId" + transaction.getMaterialId());
//                            ProductMaster productMaster = productMasterRepository.findById(transaction.getMaterialId())
//                                    .orElseThrow(() -> new ResourceNotFoundException("Product is not found"));
//                            if (productMaster != null) {
//                                qualityDashboardResponse.setMaterialName(productMaster.getProductName());
//                            }
//                            qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
//                        }
//
//
//                        TransporterMaster transporterMaster = transporterMasterRepository.findById(transaction.getTransporterId())
//                                .orElseThrow(() -> new ResourceNotFoundException("Transporter is not found"));
//                        if (transporterMaster != null) {
//                            qualityDashboardResponse.setTransporterName(transporterMaster.getTransporterName());
//                        }
//
//                        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(transaction.getVehicleId())
//                                .orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
//                        if (vehicleMaster != null) {
//                            qualityDashboardResponse.setVehicleNo(vehicleMaster.getVehicleNo());
//                        }
//
//                        qualityDashboardResponse.setIn(transaction.getVehicleIn());
//                        qualityDashboardResponse.setOut(transaction.getVehicleOut());
//                        qualityDashboardResponse.setDate(transaction.getTransactionDate());
//                        return Stream.of(qualityDashboardResponse);
//                    } else {
//                        return Stream.empty();
//                    }
//                })
//                .collect(Collectors.toList());
        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();

        for (GateEntryTransaction transaction : allTransactions) {
//            if (transaction.getTransactionType().equals("Inbound") || transaction.getTransactionType().equals("Outbound")) {
                VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
                if (transactionStatus != null && (transactionStatus.getStatusCode().equals("GWT") || transactionStatus.getStatusCode().equals("TWT") )) {
                    QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                    qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                    qualityDashboardResponse.setTpNo(transaction.getTpNo());
                    qualityDashboardResponse.setPoNo(transaction.getPoNo());
                    qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                    qualityDashboardResponse.setTransactionType(transaction.getTransactionType());



                    if (transaction.getTransactionType().equals("Inbound")) {
                        SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(transaction.getSupplierId());
                        if (supplierMaster != null) {
                            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());
                        }
                        MaterialMaster materialMaster = materialMasterRepository.findById(transaction.getMaterialId())
                                .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
                        if (materialMaster != null) {
                            qualityDashboardResponse.setMaterialName(materialMaster.getMaterialName());
                        }
//                        qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
                    }

                    if (transaction.getTransactionType().equals("Outbound")) {
                        CustomerMaster customerMaster = customerMasterRepository.findByCustomerId(transaction.getCustomerId());
                        if (customerMaster != null) {
                            qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1());
                        }
                        log.info("TicketNo" + transaction.getTicketNo());
                        log.info("MaterialId" + transaction.getMaterialId());
                        ProductMaster productMaster = productMasterRepository.findById(transaction.getMaterialId())
                                .orElseThrow(() -> new ResourceNotFoundException("Product is not found"));
                        if (productMaster != null) {
                            qualityDashboardResponse.setMaterialName(productMaster.getProductName());
                        }
                    }

                    qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
                    TransporterMaster transporterMaster = transporterMasterRepository.findById(transaction.getTransporterId())
                            .orElseThrow(() -> new ResourceNotFoundException("Transporter is not found"));
                    if (transporterMaster != null) {
                        qualityDashboardResponse.setTransporterName(transporterMaster.getTransporterName());
                    }

                    VehicleMaster vehicleMaster = vehicleMasterRepository.findById(transaction.getVehicleId())
                            .orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
                    if (vehicleMaster != null) {
                        qualityDashboardResponse.setVehicleNo(vehicleMaster.getVehicleNo());
                    }

                    qualityDashboardResponse.setIn(transaction.getVehicleIn());
                    qualityDashboardResponse.setOut(transaction.getVehicleOut());
                    qualityDashboardResponse.setDate(transaction.getTransactionDate());

                    qualityDashboardResponses.add(qualityDashboardResponse);
                }
//            }
        }

//        return qualityDashboardResponses;


        return qualityDashboardResponses;
    }

    @Override
    public String createQualityTransaction(Integer ticketNo, QualityRequest qualityRequest) {

        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }

        Optional<GateEntryTransaction> gateEntryTransactionOptional = gateEntryTransactionRepository.findById(ticketNo);
        if (!gateEntryTransactionOptional.isPresent()) {
            throw new ResourceNotFoundException("Gate Entry Transaction with ticketNo " + ticketNo + " not found");
        }

        try {
            QualityTransaction qualityTransaction = new QualityTransaction();
            qualityTransaction.setMoisture(qualityRequest.getMoisture());
            qualityTransaction.setFc(qualityRequest.getFc());
            qualityTransaction.setVm(qualityRequest.getVm());
            qualityTransaction.setAsh(qualityRequest.getAsh());
            qualityTransaction.setLoi(qualityRequest.getLoi());
            qualityTransaction.setFe_t(qualityRequest.getFe_t());
            qualityTransaction.setCarbon(qualityRequest.getCarbon());
            qualityTransaction.setFe_m(qualityRequest.getFe_m());
            qualityTransaction.setMtz(qualityRequest.getMtz());
            qualityTransaction.setSize(qualityRequest.getSize());
            qualityTransaction.setSulphur(qualityRequest.getSulphur());
            qualityTransaction.setNon_mag(qualityRequest.getNon_mag());

            qualityTransaction.setGateEntryTransaction(gateEntryTransactionOptional.get());
            QualityTransaction savedQualityTransaction = qualityTransactioRepository.save(qualityTransaction);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentTime = now.withSecond(0).withNano(0);

            // set qualityCheck in TransactionLog
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(ticketNo);
            transactionLog.setTimestamp(currentTime);
            transactionLog.setStatusCode("QCT");
            transactionLogRepository.save(transactionLog);

            // set qualityCheck in VehicleTransactionStatus
            VehicleTransactionStatus vehicleTransactionStatus = new VehicleTransactionStatus();
            vehicleTransactionStatus.setTicketNo(ticketNo);
            vehicleTransactionStatus.setStatusCode("QCT");
            vehicleTransactionStatusRepository.save(vehicleTransactionStatus);

            return "Quality added to ticket no : \"" + ticketNo + "\" successfully";
        } catch (Exception e) {
            log.error("Error occurred while at : ", e);
            return "Failed to add quality to ticket no : \"" + ticketNo + "\". Please try again.";
        }
    }

    @Override
    public QualityCreationResponse getDetailsForQualityTransaction(Integer ticketNo) {
        QualityCreationResponse qualityCreationResponse = new QualityCreationResponse();

        try {
            GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);
            if (gateEntryTransaction == null) {
                return qualityCreationResponse;
            }
            qualityCreationResponse.setTicketNo(gateEntryTransaction.getTicketNo());
            qualityCreationResponse.setTransactionDate(gateEntryTransaction.getTransactionDate());
            qualityCreationResponse.setVehicleNo(vehicleMasterRepository.findVehicleNoById(gateEntryTransaction.getVehicleId()));
            qualityCreationResponse.setVehicleInTime(gateEntryTransaction.getVehicleIn());
            qualityCreationResponse.setVehicleOutTime(gateEntryTransaction.getVehicleOut());
            qualityCreationResponse.setTransporterName(transporterMasterRepository.findTransporterNameByTransporterId(gateEntryTransaction.getTransporterId()));
            qualityCreationResponse.setTpNo(gateEntryTransaction.getTpNo());
            qualityCreationResponse.setPoNo(gateEntryTransaction.getPoNo());
            qualityCreationResponse.setChallanNo(gateEntryTransaction.getChallanNo());
            qualityCreationResponse.setTransactionType(gateEntryTransaction.getTransactionType());

            SupplierMaster supplierMaster = null;
            CustomerMaster customerMaster = null;

            if (gateEntryTransaction.getTransactionType().equals("Inbound")) {
                supplierMaster = supplierMasterRepository.findById(gateEntryTransaction.getSupplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("SupplierMaster not found"));
                qualityCreationResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                qualityCreationResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());
            } else if (gateEntryTransaction.getTransactionType().equals("Outbound")) {
                customerMaster = customerMasterRepository.findById(gateEntryTransaction.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("CustomerMaster not found"));
                qualityCreationResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                qualityCreationResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1());
            }

            String materialName = materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId());
            qualityCreationResponse.setMaterialName(materialName);
            qualityCreationResponse.setMaterialTypeName(gateEntryTransaction.getMaterialType());

            if (gateEntryTransaction.getTransactionType().equals("Inbound")) {
                List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(materialName, supplierMaster.getSupplierName(), supplierMaster.getSupplierAddressLine1());
                qualityCreationResponse.setParameters(mapQualityRangesToParameter(qualityRangeMasters, ticketNo));
            } else if (gateEntryTransaction.getTransactionType().equals("Outbound")) {
                List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByProductMasterProductName(materialName);
                qualityCreationResponse.setParameters(mapQualityRangesToParameter(qualityRangeMasters, ticketNo));
            }
        } catch (Exception e) {
            log.error("Error occurred while at : ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching quality ranges");
        }
        return qualityCreationResponse;
    }

//    private void setSupplierOrCustomerInfo(QualityCreationResponse qualityCreationResponse, Object[] info) {
////        if (info != null && info.length >= 2) {
//            String name = String.valueOf(info[0]);
//            String address = String.valueOf(info[1]);
//            qualityCreationResponse.setSupplierOrCustomerName(name);
//            qualityCreationResponse.setSupplierOrCustomerAddress(address);
//        }
//    }

    private List<QualityCreationResponse.Parameter> mapQualityRangesToParameter(List<QualityRangeMaster> qualityRangeMasters, Integer ticketNo) {
        List<QualityCreationResponse.Parameter> parameterList = new ArrayList<>();
        QualityTransaction qualityTransaction = qualityTransactioRepository.findByGateEntryTransactionTicketNo(ticketNo);
        if (qualityTransaction == null) {
            return parameterList; // or handle the case where quality transaction is not found
        }

        for (QualityRangeMaster qualityRangeMaster : qualityRangeMasters) {
            QualityCreationResponse.Parameter parameter = new QualityCreationResponse.Parameter();
            String parameterName = qualityRangeMaster.getParameterName();
            parameter.setParameterName(parameterName);
            parameter.setRangeTo(qualityRangeMaster.getRangeTo());
            parameter.setRangeFrom(qualityRangeMaster.getRangeFrom());
            setParameterValue(parameter, parameterName, qualityTransaction);
            parameterList.add(parameter);
        }
        return parameterList;
    }

    private void setParameterValue(QualityCreationResponse.Parameter parameter, String parameterName, QualityTransaction qualityTransaction) {
        switch (parameterName) {
            case "Moisture":
                parameter.setParameterValue(qualityTransaction.getMoisture());
                break;
            case "Vm":
                parameter.setParameterValue(qualityTransaction.getVm());
                break;
            case "Ash":
                parameter.setParameterValue(qualityTransaction.getAsh());
                break;
            case "Fc":
                parameter.setParameterValue(qualityTransaction.getFc());
                break;
            case "Size_20mm":
                parameter.setParameterValue(qualityTransaction.getSize());
                break;
            case "Size_03mm":
                parameter.setParameterValue(qualityTransaction.getSize());
                break;
            case "Fe_t":
                parameter.setParameterValue(qualityTransaction.getFe_t());
                break;
            case "Loi":
                parameter.setParameterValue(qualityTransaction.getLoi());
                break;
            default:
                parameter.setParameterValue(null);
                break;
        }
    }

    //Generate report for quality check

    @Override
    public ReportResponse getReportResponse(Integer ticketNo) {
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);

        if (gateEntryTransaction != null) {
            ReportResponse reportResponse = new ReportResponse();
            reportResponse.setTicketNo(gateEntryTransaction.getTicketNo());
            reportResponse.setDate(gateEntryTransaction.getTransactionDate());
            reportResponse.setTransactionType(gateEntryTransaction.getTransactionType());

            VehicleMaster vehicleMaster = vehicleMasterRepository.findById(gateEntryTransaction.getVehicleId()).
                    orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
            reportResponse.setVehicleNo(vehicleMaster.getVehicleNo());
            if(gateEntryTransaction.getTransactionType().equalsIgnoreCase("Inbound")){
                MaterialMaster materialMaster = materialMasterRepository.findById(gateEntryTransaction.getMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
                reportResponse.setMaterialOrProduct(materialMaster.getMaterialName());
                SupplierMaster supplierMaster = supplierMasterRepository.findById(gateEntryTransaction.getSupplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier is not found"));
                reportResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                reportResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());
            }
            if(gateEntryTransaction.getTransactionType().equalsIgnoreCase("Outbound")) {
                ProductMaster productMaster = productMasterRepository.findById(gateEntryTransaction.getMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
                reportResponse.setMaterialOrProduct(productMaster.getProductName());
                CustomerMaster customerMaster = customerMasterRepository.findById(gateEntryTransaction.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
                reportResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                reportResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1());

            }
            CompanyMaster companyMaster = companyMasterRepository.findById(gateEntryTransaction.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company is not found"));
            reportResponse.setCompanyName(companyMaster.getCompanyName());
            reportResponse.setCompanyAddress(companyMaster.getCompanyAddress());
            String materialType = gateEntryTransaction.getMaterialType() != null ? gateEntryTransaction.getMaterialType() : "";
            reportResponse.setMaterialTypeOrProductType(materialType);

            QualityTransaction qualityTransaction = qualityTransactioRepository.findByTicketNo(ticketNo);
            if (qualityTransaction != null) {
                reportResponse.setMoisture(qualityTransaction.getMoisture());
                reportResponse.setFc(qualityTransaction.getFc());
                reportResponse.setVm(qualityTransaction.getVm());
                reportResponse.setAsh(qualityTransaction.getAsh());
                reportResponse.setLoi(qualityTransaction.getLoi());
                reportResponse.setFe_t(qualityTransaction.getFe_t());
                reportResponse.setSize(qualityTransaction.getSize());
                reportResponse.setCarbon(qualityTransaction.getCarbon());
                reportResponse.setFe_m(qualityTransaction.getFe_m());
                reportResponse.setMtz(qualityTransaction.getMtz());
                reportResponse.setSulphur(qualityTransaction.getSulphur());
                reportResponse.setNon_mag(qualityTransaction.getNon_mag());
                return reportResponse; // Return here
            }
        }
        throw new ResourceNotFoundException("Quality transaction not found for ticketNo: " + ticketNo);
    }
}


//    public byte[] generateQualityReport(ReportResponse reportResponse) {
//        // Generate the quality report based on the ReportRequest data
//        byte[] reportBytes = generatePDFReport(reportResponse);
//        return reportBytes;
//    }

    /**
     * Generates a PDF report based on the given ReportResponse object.
     * <p>
     * This method constructs an HTML string representing the report,
     * converts it into a PDF using the iTextRenderer library,
     * and returns the resulting PDF as a byte array.
     *
     * @param reportResponse The ReportResponse object containing the data for the report.
     * @return A byte array containing the generated PDF.
     * @throws DocumentException if there is an error during PDF creation.
     * @throws IOException       if there is an IO error during PDF creation.
     */
//    private byte[] generatePDFReport(ReportResponse reportResponse) {
//
//        // Create a StringBuilder to hold the PDF content
//        StringBuilder pdfContent = new StringBuilder();
//
//        pdfContent.append("<form style=\"background-color: #f2f2f2;\">");
//
//        pdfContent.append("<h1 style=\"text-align: center;\">").append(reportResponse.getCompanyName()).append("</h1>");
//
////Add a line break (optional, adjust spacing as needed)
//        // pdfContent.append("<br/>");
//
////Add the company address as a centered sub-header
//        pdfContent.append("<h3 style=\"text-align: center; margin-bottom:15px;\">")
//                .append(reportResponse.getCompanyAddress()).append("</h3>");
//
//        // Start the table
//        pdfContent.append("<table>");
//
//        // Header row for the table
//
//        String[] headers1 = {"Ticket No:", "Date:", "Vehicle No:", "Material/Product:", "Material/Product Type:",
//                "Supplier/Customer Name:", "Transaction Type:"};
//
//        // Values array, converting all values to strings
//        String[] values1 = {reportResponse.getTicketNo().toString(), reportResponse.getDate().toString(),
//                reportResponse.getVehicleNo().toString(), reportResponse.getMaterialOrProduct().toString(),
//                reportResponse.getMaterialTypeOrProductType().toString(),
//                reportResponse.getSupplierOrCustomerName().toString(), reportResponse.getTransactionType().toString()};
//
//        // Iterate through headers and values to create rows
//        for (int i = 0; i < headers1.length; i++) {
//            pdfContent.append("<tr>");
//            pdfContent.append("<th style=\"padding: 3px;  font-weight: bold; text-align: left;\">").append(headers1[i])
//                    .append("</th>");
//            pdfContent.append("<td >").append("<div>").append(values1[i]).append("</div>").append("</td>");
//            pdfContent.append("</tr>");
//        }
//        // Close the table
//        pdfContent.append("</table>");
//
//        pdfContent.append("<h4 style=\"text-align: center;\">")
//                .append(reportResponse.getMaterialOrProduct() + " TestReport").append("</h4>");
//
//       // Start the second table for quality details
//
//		// Header row for quality details
//
//        pdfContent.append(
//                "<table style=\"text-align: left; margin: 0 auto; width:80%;border: 1px solid black; border-collapse: collapse;\">"); // Center
//        // the
//        // entire
//        // table
//
//        // Headers and values row by row
//        String[] headers = {"Moisture", "VM", "Ash", "FC", "Size20mm", "Size03mm", "Fe_t", "LOI"};
//        double[] values = {reportResponse.getMoisture(), reportResponse.getVm(), reportResponse.getAsh(),
//                reportResponse.getFc(), reportResponse.getSize(), reportResponse.getSize(),
//                reportResponse.getFe_t(), reportResponse.getLoi()};
//
//        for (int i = 0; i < headers.length; i++) {
//            pdfContent.append("<tr>");
//            pdfContent.append(
//                            "<th style=\"border: 1px solid black;padding: 5px; background-color: #e0e0e0; font-weight: bold;\">")
//                    .append(headers[i]).append("</th>");
//            pdfContent.append("<td style=\"border: 1px solid black;\">").append(values[i]).append("</td>");
//            pdfContent.append("</tr>");
//        }
//
//        // Close the table
//        pdfContent.append("</table>");
//
//        pdfContent.append("</form>");
//
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(pdfContent.toString());
//        renderer.layout();
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        renderer.createPDF(outputStream);
//        return outputStream.toByteArray();
//    }

