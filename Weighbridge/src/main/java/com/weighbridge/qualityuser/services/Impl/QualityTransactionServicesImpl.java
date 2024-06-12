package com.weighbridge.qualityuser.services.Impl;

import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.weighbridgeoperator.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.jdbc.core.StatementCreatorUtils.setParameterValue;

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


    /**
     * Retrieves paginated gate entry details for the current user, including filtering by transaction status and type.
     *
     * @return A QualityDashboardPaginationResponse object containing the list of QualityDashboardResponse objects, total pages, and total elements.
     * @throws SessionExpiredException   if the session is null or expired.
     * @throws ResourceNotFoundException if the supplier or customer related to a transaction is not found.
     */
    public List<QualityDashboardResponse> getAllGateDetails() {
        // Get the session and user information
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userId = session.getAttribute("userId").toString();
        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        // Retrieve all transactions for the user's site and company, ordered by transaction date in descending order
        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTransactionDateDesc(userSite, userCompany);
        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Process each transaction
        for (GateEntryTransaction transaction : allTransactions) {
            String statusCode = transaction.getTransactionType().equalsIgnoreCase("Inbound") ? "GWT" : "TWT";
            TransactionLog transactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), statusCode);

            if (transactionLog != null) {
                TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                if (qctTransactionLog == null) {
                    QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                    qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                    qualityDashboardResponse.setTpNo(transaction.getTpNo());
                    qualityDashboardResponse.setPoNo(transaction.getPoNo());
                    qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                    qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                    try {
                        if (transaction.getTransactionType().equalsIgnoreCase("Inbound")) {
                            SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", String.valueOf(transaction.getSupplierId())));
                            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());

                            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                            qualityDashboardResponse.setMaterialName(materialName);
                        } else {
                            CustomerMaster customerMaster = customerMasterRepository.findById(transaction.getCustomerId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", String.valueOf(transaction.getCustomerId())));
                            qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1() + "," + customerMaster.getCustomerAddressLine2());

                            String productName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                            qualityDashboardResponse.setMaterialName(productName);
                        }
                    } catch (ResourceNotFoundException e) {
                        // Log and continue to the next transaction if supplier/customer or material/product is not found
                        log.error(e.getMessage(), e);
                        continue;
                    }

                    qualityDashboardResponse.setMaterialType(transaction.getMaterialType());

                    String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                    qualityDashboardResponse.setTransporterName(transporterName);

                    String vehicleNo = vehicleMasterRepository.findVehicleNoById(transaction.getVehicleId());
                    qualityDashboardResponse.setVehicleNo(vehicleNo);

                    if (transaction.getVehicleIn() != null) {
                        qualityDashboardResponse.setIn(transaction.getVehicleIn().format(formatter));
                    }
                    if (transaction.getVehicleOut() != null) {
                        qualityDashboardResponse.setOut(transaction.getVehicleOut().format(formatter));
                    }
                    qualityDashboardResponse.setDate(transaction.getTransactionDate());

                    qualityDashboardResponses.add(qualityDashboardResponse);
                }
            }
        }

          return qualityDashboardResponses;
    }


    @Override
    public List<QualityDashboardResponse> getQCTCompletedInbound() {
        return getQCTCompletedByTransactionType("Inbound");
    }

    @Override
    public List<QualityDashboardResponse> getQCTCompletedOutbound() {
        return getQCTCompletedByTransactionType("Outbound");
    }

    private List<QualityDashboardResponse> getQCTCompletedByTransactionType(String transactionType) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userId = session.getAttribute("userId").toString();
        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTransactionDateDesc(userSite, userCompany);
        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (GateEntryTransaction transaction : allTransactions) {
            if (transaction.getTransactionType().equalsIgnoreCase(transactionType)) {
                String statusCode = transactionType.equalsIgnoreCase("Inbound") ? "GWT" : "TWT";
                TransactionLog transactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), statusCode);

                if (transactionLog != null) {
                    TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                    if (qctTransactionLog != null) {
                        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                        qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                        qualityDashboardResponse.setTpNo(transaction.getTpNo());
                        qualityDashboardResponse.setPoNo(transaction.getPoNo());
                        qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                        qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                        try {
                            if (transactionType.equalsIgnoreCase("Inbound")) {
                                SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", String.valueOf(transaction.getSupplierId())));
                                qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                                qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());

                                String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                                qualityDashboardResponse.setMaterialName(materialName);
                            } else {
                                CustomerMaster customerMaster = customerMasterRepository.findById(transaction.getCustomerId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", String.valueOf(transaction.getCustomerId())));
                                qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                                qualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1() + "," + customerMaster.getCustomerAddressLine2());

                                String productName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                                qualityDashboardResponse.setMaterialName(productName);
                            }
                        } catch (ResourceNotFoundException e) {
                            log.error(e.getMessage(), e);
                            continue;
                        }

                        qualityDashboardResponse.setMaterialType(transaction.getMaterialType());

                        String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                        qualityDashboardResponse.setTransporterName(transporterName);

                        String vehicleNo = vehicleMasterRepository.findVehicleNoById(transaction.getVehicleId());
                        qualityDashboardResponse.setVehicleNo(vehicleNo);

                        if (transaction.getVehicleIn() != null) {
                            qualityDashboardResponse.setIn(transaction.getVehicleIn().format(formatter));
                        }
                        if (transaction.getVehicleOut() != null) {
                            qualityDashboardResponse.setOut(transaction.getVehicleOut().format(formatter));
                        }
                        qualityDashboardResponse.setDate(transaction.getTransactionDate());

                        qualityDashboardResponses.add(qualityDashboardResponse);
                    }
                }
            }
        }

        return qualityDashboardResponses;
    }

    @Override
    public int getInboundQCTCompletedSize() {
        List<QualityDashboardResponse> inboundResponses = getQCTCompletedInbound();
        return inboundResponses.size();
    }

    @Override
    public int getOutboundQCTCompletedSize() {
        List<QualityDashboardResponse> outboundResponses = getQCTCompletedOutbound();
        return outboundResponses.size();
    }

    @Override
    public int getTotalQCTCompletedSize() {
        List<QualityDashboardResponse> completedList = getQCTCompleted();
        return completedList.size();
    }


    @Override
    public List<QualityDashboardResponse> getQCTCompleted() {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userId = session.getAttribute("userId").toString();
        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        // Retrieve all transactions for the user's site and company, ordered by transaction date in descending order
        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTransactionDateDesc(userSite, userCompany);
        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Process each transaction
        for (GateEntryTransaction transaction : allTransactions) {
            String statusCode = transaction.getTransactionType().equalsIgnoreCase("Inbound") ? "GWT" : "TWT";
            TransactionLog transactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), statusCode);

            if (transactionLog != null) {
                TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                if (qctTransactionLog != null) {
                    QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                    qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                    qualityDashboardResponse.setTpNo(transaction.getTpNo());
                    qualityDashboardResponse.setPoNo(transaction.getPoNo());
                    qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                    qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                    try {
                        if (transaction.getTransactionType().equalsIgnoreCase("Inbound")) {
                            SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", String.valueOf(transaction.getSupplierId())));
                            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());

                            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                            qualityDashboardResponse.setMaterialName(materialName);
                        } else {
                            CustomerMaster customerMaster = customerMasterRepository.findById(transaction.getCustomerId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", String.valueOf(transaction.getCustomerId())));
                            qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1() + "," + customerMaster.getCustomerAddressLine2());

                            String productName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                            qualityDashboardResponse.setMaterialName(productName);
                        }
                    } catch (ResourceNotFoundException e) {
                        log.error(e.getMessage(), e);
                        continue;
                    }

                    qualityDashboardResponse.setMaterialType(transaction.getMaterialType());

                    String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                    qualityDashboardResponse.setTransporterName(transporterName);

                    String vehicleNo = vehicleMasterRepository.findVehicleNoById(transaction.getVehicleId());
                    qualityDashboardResponse.setVehicleNo(vehicleNo);

                    if (transaction.getVehicleIn() != null) {
                        qualityDashboardResponse.setIn(transaction.getVehicleIn().format(formatter));
                    }
                    if (transaction.getVehicleOut() != null) {
                        qualityDashboardResponse.setOut(transaction.getVehicleOut().format(formatter));
                    }
                    qualityDashboardResponse.setDate(transaction.getTransactionDate());
                    QualityTransaction qualityTransaction = qualityTransactionRepository.findByTicketNo(transaction.getTicketNo());
                    if (qualityTransaction == null) {
                        qualityDashboardResponse.setQualityParametersPresent(false);
                    } else {
                        qualityDashboardResponse.setQualityParametersPresent(true);
                    }
                    qualityDashboardResponses.add(qualityDashboardResponse);
                }
            }
        }

       return qualityDashboardResponses;
    }


    @Override
    public List<String> getAllMaterialAndProductNames() {
        List<String> materialNames = materialMasterRepository.findAllMaterialNameByMaterialStatus("ACTIVE");
        List<String> productNames = productMasterRepository.findAllProductNameByProductStatus("ACTIVE");
        List<String> allMaterialAndProductNames = new ArrayList<>();
        allMaterialAndProductNames.addAll(materialNames);
        allMaterialAndProductNames.addAll(productNames);
        return allMaterialAndProductNames;
    }

    @Override
    public List<String> getAllProductNames() {
        return productMasterRepository.findAllProductNameByProductStatus("ACTIVE");
    }

    @Override
    public List<String> getAllMaterialNames() {
        return materialMasterRepository.findAllMaterialNameByMaterialStatus("ACTIVE");
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
                .orElseThrow(() -> new ResourceNotFoundException("Gate entry transaction is not found with " + ticketNo));

        QualityTransaction qualityTransaction = new QualityTransaction();
        StringBuilder qualityRangeIds = new StringBuilder();
        StringBuilder qualityValues = new StringBuilder();
        boolean isQualityGood=true;
        if (gateEntryTransaction.getTransactionType().equals("Inbound")) {
            String materialName = materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId());
            SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(gateEntryTransaction.getSupplierId());
            String supplierAddress = supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2();
            for (Map.Entry<String, Double> entry : transactionRequest.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Long qualityId = qualityRangeMasterRepository.findQualityRangeIdByParameterNameAndMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(key, materialName, supplierMaster.getSupplierName(), supplierAddress);
                QualityRangeMaster qualityRangeMaster=qualityRangeMasterRepository.findById(qualityId).orElseThrow(()->new ResourceNotFoundException("Range not found for qualityId:"+qualityId));

                if(value < qualityRangeMaster.getRangeFrom() || value > qualityRangeMaster.getRangeTo()){
                    if(value==null){
                        continue;
                    }
                    isQualityGood=false;
                }
                qualityRangeIds.append(qualityId).append(",");
                qualityValues.append(value).append(",");

            }
            qualityTransaction.setGateEntryTransaction(gateEntryTransaction);
            qualityTransaction.setQualityRangeId(qualityRangeIds.toString().replaceAll(",$", "").trim());
            qualityTransaction.setQualityValues(qualityValues.toString().replaceAll(",$", "").trim());
            qualityTransaction.setIsQualityGood(isQualityGood);
            qualityTransactionRepository.save(qualityTransaction);
        }
        if (gateEntryTransaction.getTransactionType().equals("Outbound")) {
            String productName = productMasterRepository.findProductNameByProductId(gateEntryTransaction.getMaterialId());
            for (Map.Entry<String, Double> entry : transactionRequest.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Long qualityId = qualityRangeMasterRepository.findQualityRangeIdByParameterNameAndProductMasterProductName(key, productName);
                QualityRangeMaster qualityRangeMaster=qualityRangeMasterRepository.findById(qualityId).orElseThrow(()->new ResourceNotFoundException("Range not found for qualityId:"+qualityId));

                if(value < qualityRangeMaster.getRangeFrom() || value > qualityRangeMaster.getRangeTo()){
                    if(value==null){
                        continue;
                    }
                    isQualityGood=false;
                }
                qualityRangeIds.append(qualityId).append(",");
                qualityValues.append(value).append(",");
            }
            qualityTransaction.setGateEntryTransaction(gateEntryTransaction);
            qualityTransaction.setQualityRangeId(qualityRangeIds.toString().replaceAll(",$", "").trim());
            qualityTransaction.setQualityValues(qualityValues.toString().replaceAll(",$", "").trim());
            qualityTransaction.setIsQualityGood(isQualityGood);
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


//Generate report for quality check
    @Override
    public ReportResponse getReportResponse(Integer ticketNo) {
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);
        if (gateEntryTransaction != null) {
            VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(gateEntryTransaction.getTicketNo());
            ReportResponse reportResponse = new ReportResponse();
            reportResponse.setTicketNo(gateEntryTransaction.getTicketNo());
            reportResponse.setDate(String.valueOf(gateEntryTransaction.getTransactionDate()));
            reportResponse.setTransactionType(gateEntryTransaction.getTransactionType());
            VehicleMaster vehicleMaster = vehicleMasterRepository.findById(gateEntryTransaction.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));
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
                String[] qualityRangeIds = qualityTransaction.getQualityRangeId().split(",");
                String[] qualityValues = qualityTransaction.getQualityValues().split(",");
                Map<Long, String> qualityParameters = qualityRangeMasterRepository.findAllById(Arrays.stream(qualityRangeIds)
                                .map(Long::valueOf).collect(Collectors.toList()))
                        .stream().collect(Collectors.toMap(QualityRangeMaster::getQualityRangeId, QualityRangeMaster::getParameterName));
                Map<String, Double> dynamicQualityParameters = new HashMap<>();
                for (int i = 0; i < qualityRangeIds.length; i++) {
                    Long qualityRangeId = Long.valueOf(qualityRangeIds[i]);
                    String parameterName = qualityParameters.get(qualityRangeId);
                    String value = qualityValues[i];
                    dynamicQualityParameters.put(parameterName, Double.valueOf(value));
                }
                reportResponse.setQualityParameters(dynamicQualityParameters);
                //for enable and disable report for quality user
                //set the quality parameters present
                reportResponse.setQualityParametersPresent(!dynamicQualityParameters.isEmpty());
            } else {
                reportResponse.setQualityParametersPresent(false);
            }
            return reportResponse;
        }
        throw new ResourceNotFoundException("Quality transaction not found for ticketNo: " + ticketNo);
    }

    @Override
    public void passQualityTransaction(Integer ticketNo) {
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again!");
        }
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findById(ticketNo)
                .orElseThrow(() -> new ResourceNotFoundException("Gate entry transaction is not found with " + ticketNo));
        VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(gateEntryTransaction.getTicketNo());
        if (transactionStatus == null) {
            throw new ResourceNotFoundException("Vehicle transaction status is not found Ticket no:" + ticketNo);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentTime = now.withSecond(0).withNano(0);

        try {
            // set qualityCheck in TransactionLog
            Optional<TransactionLog> isExist = Optional.ofNullable(transactionLogRepository.findByTicketNoAndStatusCode(gateEntryTransaction.getTicketNo(), "QCT"));
            if (isExist == null) {
                TransactionLog transactionLog = new TransactionLog();
                transactionLog.setUserId(userId);
                transactionLog.setTicketNo(ticketNo);
                transactionLog.setTimestamp(currentTime);
                transactionLog.setStatusCode("QCT");
                transactionLogRepository.save(transactionLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // set qualityCheck in VehicleTransactionStatus
        VehicleTransactionStatus vehicleTransactionStatus = new VehicleTransactionStatus();
        vehicleTransactionStatus.setTicketNo(ticketNo);
        vehicleTransactionStatus.setStatusCode("QCT");
        vehicleTransactionStatusRepository.save(vehicleTransactionStatus);
    }

    @Override
    public List<QualityDashboardResponse> getInboundTransaction() {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userId = session.getAttribute("userId").toString();
        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        // Retrieve all transactions for the user's site and company

        List<GateEntryTransaction> inboundTransaction = gateEntryTransactionRepository.findByTransactionTypeAndSiteIdAndCompanyIdOrderByTransactionDate("Inbound", userSite, userCompany);
        return processTransaction(inboundTransaction);
    }

    @Override
    public List<QualityDashboardResponse> getOutboundTransaction() {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userId = session.getAttribute("userId").toString();
        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        // Retrieve all transactions for the user's site and company, ordered by transaction date in descending order
        List<GateEntryTransaction> outboundTransaction = gateEntryTransactionRepository.findByTransactionTypeAndSiteIdAndCompanyIdOrderByTransactionDate("Outbound", userSite, userCompany);
        return processTransaction(outboundTransaction);
    }

    @Override
    public int getInboundTransactionSize() {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        // Retrieve all inbound transactions for the user's site and company
        List<GateEntryTransaction> inboundTransaction = gateEntryTransactionRepository.findByTransactionTypeAndSiteIdAndCompanyIdOrderByTransactionDate("Inbound", userSite, userCompany);
        return processTransaction(inboundTransaction).size();
    }

    @Override
    public int getOutboundTransactionSize() {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        // Retrieve all outbound transactions for the user's site and company
        List<GateEntryTransaction> outboundTransaction = gateEntryTransactionRepository.findByTransactionTypeAndSiteIdAndCompanyIdOrderByTransactionDate("Outbound", userSite, userCompany);
        return processTransaction(outboundTransaction).size();
    }

    @Override
    public int getTotalTransactionSize() {
        int inboundSize = getInboundTransactionSize();
        int outboundSize = getOutboundTransactionSize();
        return inboundSize + outboundSize;
    }


    private List<QualityDashboardResponse> processTransaction(List<GateEntryTransaction> transactions) {
        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        for (GateEntryTransaction transaction : transactions) {
            String statusCode = transaction.getTransactionType().equalsIgnoreCase("Inbound") ? "GWT" : "TWT";
            TransactionLog transactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), statusCode);

            if (transactionLog != null) {
                TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                if (qctTransactionLog == null) {
                    QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                    qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                    qualityDashboardResponse.setTpNo(transaction.getTpNo());
                    qualityDashboardResponse.setPoNo(transaction.getPoNo());
                    qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                    qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                    try {
                        if (transaction.getTransactionType().equalsIgnoreCase("Inbound")) {
                            SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", String.valueOf(transaction.getSupplierId())));
                            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());

                            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                            qualityDashboardResponse.setMaterialName(materialName);
                        } else {
                            CustomerMaster customerMaster = customerMasterRepository.findById(transaction.getCustomerId())
                                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", String.valueOf(transaction.getCustomerId())));
                            qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                            qualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine1() + "," + customerMaster.getCustomerAddressLine2());

                            String productName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                            qualityDashboardResponse.setMaterialName(productName);
                        }
                    } catch (ResourceNotFoundException e) {
                        log.error(e.getMessage(), e);
                        continue;
                    }

                    qualityDashboardResponse.setMaterialType(transaction.getMaterialType());

                    String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                    qualityDashboardResponse.setTransporterName(transporterName);

                    String vehicleNo = vehicleMasterRepository.findVehicleNoById(transaction.getVehicleId());
                    qualityDashboardResponse.setVehicleNo(vehicleNo);

                    if (transaction.getVehicleIn() != null) {
                        qualityDashboardResponse.setIn(transaction.getVehicleIn().format(formatter));
                    }
                    if (transaction.getVehicleOut() != null) {
                        qualityDashboardResponse.setOut(transaction.getVehicleOut().format(formatter));
                    }
                    qualityDashboardResponse.setDate(transaction.getTransactionDate());

                    qualityDashboardResponses.add(qualityDashboardResponse);
                }
            }
        }

        return qualityDashboardResponses;
    }


}
