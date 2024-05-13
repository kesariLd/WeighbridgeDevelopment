package com.weighbridge.qualityuser.services.Impl;

import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.entities.SupplierMaster;
import com.weighbridge.admin.entities.TransporterMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
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
                                          QualityRangeMasterRepository qualityRangeMasterRepository) {
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

        List<QualityDashboardResponse> qualityDashboardResponses = allTransactions.stream()
                .filter(transaction -> transaction.getTransactionType().equals("Inbound") || transaction.getTransactionType().equals("Outbound"))
                .flatMap(transaction -> {
                    VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
                    if (transactionStatus != null && (transactionStatus.getStatusCode().equals("GWT") || transactionStatus.getStatusCode().equals("TWT"))) {
                        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                        qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                        qualityDashboardResponse.setTpNo(transaction.getTpNo());
                        qualityDashboardResponse.setPoNo(transaction.getPoNo());
                        qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                        qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                        SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(transaction.getSupplierId());
                        if(supplierMaster != null) {
                            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());
                        }

                        MaterialMaster materialMaster = materialMasterRepository.findById(transaction.getMaterialId())
                                .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
                        if(materialMaster!= null) {
                            qualityDashboardResponse.setMaterialName(materialMaster.getMaterialName());
                            qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
                        }

                        TransporterMaster transporterMaster = transporterMasterRepository.findById(transaction.getTransporterId())
                                .orElseThrow(() -> new ResourceNotFoundException("Transporter is not found"));
                        if(transporterMaster != null) {
                            qualityDashboardResponse.setTransporterName(transporterMaster.getTransporterName());
                        }

                        VehicleMaster vehicleMaster = vehicleMasterRepository.findById(transaction.getVehicleId())
                                .orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
                        if(vehicleMaster != null) {
                            qualityDashboardResponse.setVehicleNo(vehicleMaster.getVehicleNo());
                        }

                        qualityDashboardResponse.setIn(transaction.getVehicleIn());
                        qualityDashboardResponse.setOut(transaction.getVehicleOut());
                        qualityDashboardResponse.setDate(transaction.getTransactionDate());
                        return Stream.of(qualityDashboardResponse);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

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
            qualityTransaction.setSize_03mm(qualityRequest.getSize_03mm());
            qualityTransaction.setSize_20mm(qualityRequest.getSize_20mm());
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

            if ("Inbound".equals(gateEntryTransaction.getTransactionType())) {
                Object[] supplierInfo = supplierMasterRepository.findSupplierNameBySupplierId(gateEntryTransaction.getSupplierId());
                setSupplierOrCustomerInfo(qualityCreationResponse, supplierInfo);
            } else if ("Outbound".equals(gateEntryTransaction.getTransactionType())) {
                Object[] customerInfo = customerMasterRepository.findCustomerNameBycustomerId(ticketNo);
                setSupplierOrCustomerInfo(qualityCreationResponse, customerInfo);
            }

            String materialName = materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId());
            qualityCreationResponse.setMaterialName(materialName);
            qualityCreationResponse.setMaterialTypeName(gateEntryTransaction.getMaterialType());

            List<QualityRangeMaster> qualityRangeMasters = qualityRangeMasterRepository.findByMaterialMasterMaterialName(materialName);
            qualityCreationResponse.setParameters(mapQualityRangesToParameter(qualityRangeMasters, ticketNo));
        } catch (Exception e) {
            log.error("Error occurred while at : ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching quality ranges");
        }
        return qualityCreationResponse;
    }

    private void setSupplierOrCustomerInfo(QualityCreationResponse qualityCreationResponse, Object[] info) {
        if (info != null && info.length >= 2) {
            String name = (String) info[0];
            String address = (String) info[1];
            qualityCreationResponse.setSupplierOrCustomerName(name);
            qualityCreationResponse.setSupplierOrCustomerAddress(address);
        }
    }

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
            case "moisture":
                parameter.setParameterValue(qualityTransaction.getMoisture());
                break;
            case "vm":
                parameter.setParameterValue(qualityTransaction.getVm());
                break;
            case "ash":
                parameter.setParameterValue(qualityTransaction.getAsh());
                break;
            case "fc":
                parameter.setParameterValue(qualityTransaction.getFc());
                break;
            case "size_20mm":
                parameter.setParameterValue(qualityTransaction.getSize_20mm());
                break;
            case "size_03mm":
                parameter.setParameterValue(qualityTransaction.getSize_03mm());
                break;
            case "fe_t":
                parameter.setParameterValue(qualityTransaction.getFe_t());
                break;
            case "loi":
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

            MaterialMaster materialMaster = materialMasterRepository.findById(gateEntryTransaction.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
            reportResponse.setMaterialOrProduct(materialMaster.getMaterialName());
            reportResponse.setMaterialTypeOrProductType(materialMaster.getMaterialTypes().toString());
            SupplierMaster supplierMaster = supplierMasterRepository.findById(gateEntryTransaction.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier is not found"));
            reportResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());


            QualityTransaction qualityTransaction = qualityTransactioRepository.findByTicketNo(ticketNo);
            if (qualityTransaction != null) {
                reportResponse.setMoisture(qualityTransaction.getMoisture());
                reportResponse.setFc(qualityTransaction.getFc());
                reportResponse.setVm(qualityTransaction.getVm());
                reportResponse.setAsh(qualityTransaction.getAsh());
                reportResponse.setLoi(qualityTransaction.getLoi());
                reportResponse.setFe_t(qualityTransaction.getFe_t());
                reportResponse.setSize_03mm(qualityTransaction.getSize_03mm());
                reportResponse.setSize_20mm(qualityTransaction.getSize_20mm());

                return reportResponse; // Return here
            }
        }

        throw new ResourceNotFoundException("Quality transaction not found for ticketNo: " + ticketNo);
    }

    public byte[] generateQualityReport(ReportResponse reportResponse) {
        // Generate the quality report based on the ReportRequest data
        byte[] reportBytes = generatePDFReport(reportResponse);
        return reportBytes;
    }

    private byte[] generatePDFReport(ReportResponse reportResponse) {
        StringBuilder pdfContent = new StringBuilder();

        pdfContent.append("Quality Report");
        pdfContent.append("Ticket No:").append(reportResponse.getTicketNo()).append("\n");
        pdfContent.append("Date: ").append(reportResponse.getDate()).append("\n");
        pdfContent.append("Vehicle No: ").append(reportResponse.getVehicleNo()).append("\n");
        pdfContent.append("Material/Product: ").append(reportResponse.getMaterialOrProduct()).append("\n");
        pdfContent.append("Material/Product Type: ").append(reportResponse.getMaterialTypeOrProductType()).append("\n");
        pdfContent.append("Supplier/Customer Name: ").append(reportResponse.getSupplierOrCustomerName()).append("\n");
        pdfContent.append("Transaction Type: ").append(reportResponse.getTransactionType()).append("\n");
        pdfContent.append("Moisture: ").append(reportResponse.getMoisture()).append("\n");
        pdfContent.append("VM: ").append(reportResponse.getVm()).append("\n");
        pdfContent.append("Ash: ").append(reportResponse.getAsh()).append("\n");
        pdfContent.append("FC: ").append(reportResponse.getFc()).append("\n");
        pdfContent.append("Size 20mm: ").append(reportResponse.getSize_20mm()).append("\n");
        pdfContent.append("Size 03mm: ").append(reportResponse.getSize_03mm()).append("\n");
        pdfContent.append("Fe_t: ").append(reportResponse.getFe_t()).append("\n");
        pdfContent.append("LOI: ").append(reportResponse.getLoi()).append("\n");

        return pdfContent.toString().getBytes();

    }
}



