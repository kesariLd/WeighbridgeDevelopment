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
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class QualityTransactionServicesImpl implements QualityTransactionService {

    private final QualityTransactionRepository qualityTransactionRepository;
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

    public QualityTransactionServicesImpl(QualityTransactionRepository qualityTransactionRepository,
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
        this.qualityTransactionRepository = qualityTransactionRepository;
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

        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();

        for (GateEntryTransaction transaction : allTransactions) {
//            if (transaction.getTransactionType().equals("Inbound") || transaction.getTransactionType().equals("Outbound")) {
            VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
            if (transactionStatus != null && (transactionStatus.getStatusCode().equals("GWT") || transactionStatus.getStatusCode().equals("TWT"))) {
                QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                qualityDashboardResponse.setTpNo(transaction.getTpNo());
                qualityDashboardResponse.setPoNo(transaction.getPoNo());
                qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                if (transaction.getTransactionType().equals("Inbound")) {
                    supplierMasterRepository.findSupplierNameBySupplierId(transaction.getSupplierId());
                    Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
                    // Inbound transaction
                    Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                    if (supplierInfo != null && supplierInfo.length >= 2) {
                        String supplierName = (String) supplierInfo[0];
                        String supplierAddress = (String) supplierInfo[1];
                        qualityDashboardResponse.setSupplierOrCustomerName(supplierName);
                        qualityDashboardResponse.setSupplierOrCustomerAddress(supplierAddress);
                    }
                    String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                    if (materialName != null) {
                        qualityDashboardResponse.setMaterialName(materialName);
                    }
//           qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
                }

                if (transaction.getTransactionType().equals("Outbound")) {
                    Object[] customerNamebyId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());
                    // Inbound transaction
                    Object[] customerInfo = (Object[]) customerNamebyId[0];
                    if (customerInfo != null && customerInfo.length >= 2) {
                        String customerName = (String) customerInfo[0];
                        String customerAddress = (String) customerInfo[1];
                        qualityDashboardResponse.setSupplierOrCustomerName(customerName);
                        qualityDashboardResponse.setSupplierOrCustomerAddress(customerAddress);
                    }
                    log.info("TicketNo" + transaction.getTicketNo());
                    log.info("MaterialId" + transaction.getMaterialId());
                    String productNameByProductId = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                    if (productNameByProductId != null) {
                        qualityDashboardResponse.setMaterialName(productNameByProductId);
                    }
                }

                qualityDashboardResponse.setMaterialType(transaction.getMaterialType());
                String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                if (transporterName != null) {
                    qualityDashboardResponse.setTransporterName(transporterName);
                }

                String vehicleNoById = vehicleMasterRepository.findVehicleNoById(transaction.getVehicleId());
                if (vehicleNoById != null) {
                    qualityDashboardResponse.setVehicleNo(vehicleNoById);
                }

                qualityDashboardResponse.setIn(transaction.getVehicleIn());
                qualityDashboardResponse.setOut(transaction.getVehicleOut());
                qualityDashboardResponse.setDate(transaction.getTransactionDate());
                qualityDashboardResponses.add(qualityDashboardResponse);
            }

        }
        return qualityDashboardResponses;
    }

    @Transactional
    @Override
    public String createQualityTransaction(Integer ticketNo, Map<String, Double> transactionRequest) {

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

        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findById(ticketNo)
                .orElseThrow(() -> new ResourceNotFoundException("Gate entry transaction is not found with "+ ticketNo));

        if (gateEntryTransaction.getTransactionType().equals("Inbound")) {
            String materialName = materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId());
            QualityTransaction qualityTransaction = new QualityTransaction();
            StringBuilder qualityRangeIds = new StringBuilder();
            StringBuilder qualityValues = new StringBuilder();
            SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(gateEntryTransaction.getSupplierId());
            for (Map.Entry<String, Double> entry : transactionRequest.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Long qualityId = qualityRangeMasterRepository.findQualityRangeIdByParameterNameAndMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(key, materialName, supplierMaster.getSupplierName(), supplierMaster.getSupplierAddressLine1());
                qualityRangeIds.append(qualityId).append(",");
                qualityValues.append(value).append(",");
            }
            qualityTransaction.setGateEntryTransaction(gateEntryTransaction);
            qualityTransaction.setQualityRangeId(qualityRangeIds.toString().replaceAll(",$", "").trim());
            qualityTransaction.setQualityValues(qualityValues.toString().replaceAll(",$", "").trim());
            qualityTransactionRepository.save(qualityTransaction);
        }

        if (gateEntryTransaction.getTransactionType().equals("Outbound")) {
            String productName = productMasterRepository.findProductNameByProductId(gateEntryTransaction.getMaterialId());
            QualityTransaction qualityTransaction = new QualityTransaction();
            StringBuilder qualityValues = new StringBuilder();
            StringBuilder qualityRangeIds = new StringBuilder();
            for (Map.Entry<String, Double> entry : transactionRequest.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Long qualityId = qualityRangeMasterRepository.findQualityRangeIdByParameterNameAndProductMasterProductName(key, productName);
                qualityRangeIds.append(qualityId).append(",");
                qualityValues.append(value).append(",");
            }
            qualityTransaction.setGateEntryTransaction(gateEntryTransaction);
            qualityTransaction.setQualityRangeId(qualityRangeIds.toString().replaceAll(",$", "").trim());
            qualityTransaction.setQualityValues(qualityValues.toString().replaceAll(",$", "").trim());
            qualityTransactionRepository.save(qualityTransaction);
        }


        try {
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


    private List<QualityCreationResponse.Parameter> mapQualityRangesToParameter(List<QualityRangeMaster> qualityRangeMasters, Integer ticketNo) {
        List<QualityCreationResponse.Parameter> parameterList = new ArrayList<>();

        QualityTransaction qualityTransaction = qualityTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);
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
//                parameter.setParameterValue(qualityTransaction.getMoisture());
                break;
            case "Vm":
//                parameter.setParameterValue(qualityTransaction.getVm());
                break;
            case "Ash":
//                parameter.setParameterValue(qualityTransaction.getAsh());
                break;
            case "Fc":
//                parameter.setParameterValue(qualityTransaction.getFc());
                break;
            case "Size_20mm":
//                parameter.setParameterValue(qualityTransaction.getSize());
                break;
            case "Size_03mm":
//                parameter.setParameterValue(qualityTransaction.getSize());
                break;
            case "Fe_t":
//                parameter.setParameterValue(qualityTransaction.getFe_t());
                break;
            case "Loi":
//                parameter.setParameterValue(qualityTransaction.getLoi());
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
            if (gateEntryTransaction.getTransactionType().equalsIgnoreCase("Inbound")) {
                MaterialMaster materialMaster = materialMasterRepository.findById(gateEntryTransaction.getMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("Material is not found"));
                reportResponse.setMaterialOrProduct(materialMaster.getMaterialName());
                SupplierMaster supplierMaster = supplierMasterRepository.findById(gateEntryTransaction.getSupplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier is not found"));
                reportResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                reportResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1());
            }
            if (gateEntryTransaction.getTransactionType().equalsIgnoreCase("Outbound")) {
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


            QualityTransaction qualityTransaction = qualityTransactionRepository.findByTicketNo(ticketNo);
            if (qualityTransaction != null) {
//                reportResponse.setMoisture(qualityTransaction.getMoisture());
//                reportResponse.setFc(qualityTransaction.getFc());
//                reportResponse.setVm(qualityTransaction.getVm());
//                reportResponse.setAsh(qualityTransaction.getAsh());
//                reportResponse.setLoi(qualityTransaction.getLoi());
//                reportResponse.setFe_t(qualityTransaction.getFe_t());
//                reportResponse.setSize(qualityTransaction.getSize());
//                reportResponse.setCarbon(qualityTransaction.getCarbon());
//                reportResponse.setFe_m(qualityTransaction.getFe_m());
//                reportResponse.setMtz(qualityTransaction.getMtz());
//                reportResponse.setSulphur(qualityTransaction.getSulphur());
//                reportResponse.setNon_mag(qualityTransaction.getNon_mag());
                return reportResponse; // Return here
            }
        }
        throw new ResourceNotFoundException("Quality transaction not found for ticketNo: " + ticketNo);
    }
}


