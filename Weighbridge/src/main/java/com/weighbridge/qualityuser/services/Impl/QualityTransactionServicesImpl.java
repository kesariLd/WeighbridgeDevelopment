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
import com.weighbridge.qualityuser.payloads.QualityDashboardPaginationResponse;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.payloads.ReportResponse;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.qualityuser.services.QualityTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    public QualityDashboardPaginationResponse getAllGateDetails(Pageable pageable) {

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

        Page<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTransactionDateDesc(userSite, userCompany, pageable);
        List<QualityDashboardResponse> qualityDashboardResponses = new ArrayList<>();

        for (GateEntryTransaction transaction : allTransactions) {
            VehicleTransactionStatus transactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
            if (transactionStatus != null && (transactionStatus.getStatusCode().equals("GWT") || transactionStatus.getStatusCode().equals("TWT"))) {
                QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
                qualityDashboardResponse.setTpNo(transaction.getTpNo());
                qualityDashboardResponse.setPoNo(transaction.getPoNo());
                qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
                qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

                if (transaction.getTransactionType().equals("Inbound")) {
                    SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", String.valueOf(transaction.getSupplierId())));
                    qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                    String supplierAddress = supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2();
                    qualityDashboardResponse.setSupplierOrCustomerAddress(supplierAddress);
                    String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                    if (materialName != null) {
                        qualityDashboardResponse.setMaterialName(materialName);
                    }
                }

                if (transaction.getTransactionType().equals("Outbound")) {
                    CustomerMaster customerMaster = customerMasterRepository.findById(transaction.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", String.valueOf(transaction.getCustomerId())));
                    qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                    String customerAddress = customerMaster.getCustomerAddressLine1() + "," + customerMaster.getCustomerAddressLine2();
                    qualityDashboardResponse.setSupplierOrCustomerAddress(customerAddress);
                    log.info("TicketNo: " + transaction.getTicketNo());
                    log.info("MaterialId: " + transaction.getMaterialId());
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

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
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

        QualityDashboardPaginationResponse qualityDashboardPaginationResponse = new QualityDashboardPaginationResponse();
        qualityDashboardPaginationResponse.setQualityDashboardResponseList(qualityDashboardResponses);
        qualityDashboardPaginationResponse.setTotalPages(allTransactions.getTotalPages());
        qualityDashboardPaginationResponse.setTotalElements(allTransactions.getTotalElements());
        return qualityDashboardPaginationResponse;
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

        if (gateEntryTransaction.getTransactionType().equals("Inbound")) {
            String materialName = materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId());
            QualityTransaction qualityTransaction = new QualityTransaction();
            StringBuilder qualityRangeIds = new StringBuilder();
            StringBuilder qualityValues = new StringBuilder();
            SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(gateEntryTransaction.getSupplierId());
            String supplierAddress = supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2();
            for (Map.Entry<String, Double> entry : transactionRequest.entrySet()) {
                String key = entry.getKey();
                Double value = entry.getValue();
                Long qualityId = qualityRangeMasterRepository.findQualityRangeIdByParameterNameAndMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(key, materialName, supplierMaster.getSupplierName(), supplierAddress);
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

    @Override
    public List<QualityDashboardResponse> searchByTicketNoVehicleNoSupplierAndSupplierAddress(Integer ticketNo, String vehicleNo, String supplierOrCustomerName, String supplierOrCustomerAddress) {
        List<QualityDashboardResponse> responses = new ArrayList<>();

        try {
            // Search by ticketNo
            if (ticketNo != null) {
                QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                GateEntryTransaction transactionByTicketNo = gateEntryTransactionRepository.findByTicketNo(ticketNo);
                if (transactionByTicketNo != null) {
                    setQualityDashboardResponseDetails(qualityDashboardResponse, transactionByTicketNo);
                    responses.add(qualityDashboardResponse);
                    return responses;
                }
            }

            // Search for supplierName and supplierAddress
            if (supplierOrCustomerName != null || supplierOrCustomerAddress != null || ticketNo != null) {
                List<SupplierMaster> supplierMasters = supplierMasterRepository.findBySupplierNameContainingOrSupplierAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
                List<GateEntryTransaction> transactions = new ArrayList<>();
                for (SupplierMaster supplierMaster : supplierMasters) {
                    List<GateEntryTransaction> gateEntryTransaction;
                    if (ticketNo != null) {
                        gateEntryTransaction = Optional.ofNullable(gateEntryTransactionRepository.findBySupplierIdAndTicketNoOrderByTicketNoDesc(supplierMaster.getSupplierId(), ticketNo))
                                .orElseThrow(() -> new ResourceNotFoundException("Supplier is not found with id" + supplierMaster.getSupplierId() + " or ticketNo " + ticketNo + " is not found."));
                    } else {
                        gateEntryTransaction = Optional.ofNullable(gateEntryTransactionRepository.findBySupplierIdOrderByTicketNoDesc(supplierMaster.getSupplierId()))
                                .orElseThrow(() -> new ResourceNotFoundException("Supplier is not found with id" + supplierMaster.getSupplierId()));
                    }
                    transactions.addAll(gateEntryTransaction);
                }
                for (GateEntryTransaction gateEntryTransaction : transactions) {
                    QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                    setQualityDashboardResponseDetails(qualityDashboardResponse, gateEntryTransaction);
                    responses.add(qualityDashboardResponse);
                }
                return responses;
            }


            // Search by vehicleNo
            if (vehicleNo != null) {
                VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);
                if (vehicleMaster != null) {
                    List<GateEntryTransaction> transactionsByVehicleId = gateEntryTransactionRepository.findByVehicleIdOrderByTicketNoDesc(vehicleMaster.getId());
                    Collections.sort(transactionsByVehicleId, Comparator.comparing(GateEntryTransaction::getTicketNo).reversed());
                    for (GateEntryTransaction gateEntryTransaction : transactionsByVehicleId) {
                        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                        setQualityDashboardResponseDetails(qualityDashboardResponse, gateEntryTransaction);
                        responses.add(qualityDashboardResponse);
                    }
                }
                return responses;
            }


        } catch (Exception e) {
            log.error("Error occurred while searching: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while searching");
        }
        return responses;
    }

    private void setQualityDashboardResponseDetails(QualityDashboardResponse qualityDashboardResponse, GateEntryTransaction transaction) {


        qualityDashboardResponse.setTicketNo(transaction.getTicketNo());
        qualityDashboardResponse.setTpNo(transaction.getTpNo());
        qualityDashboardResponse.setPoNo(transaction.getPoNo());
        qualityDashboardResponse.setChallanNo(transaction.getChallanNo());
        qualityDashboardResponse.setTransactionType(transaction.getTransactionType());

        // Inbound transaction details
        if (transaction.getTransactionType().equals("Inbound")) {
            supplierMasterRepository.findSupplierNameBySupplierId(transaction.getSupplierId());
            SupplierMaster supplierMaster = supplierMasterRepository.findById(transaction.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", String.valueOf(transaction.getSupplierId())));
            qualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
            String supplierAddress = supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2();
            qualityDashboardResponse.setSupplierOrCustomerAddress(supplierAddress);
            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
            if (materialName != null) {
                qualityDashboardResponse.setMaterialName(materialName);
            }
        }

        // Outbound transaction details
        if (transaction.getTransactionType().equals("Outbound")) {
            CustomerMaster customerMaster = customerMasterRepository.findById(transaction.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", String.valueOf(transaction.getCustomerId())));
            qualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
            String customerAddress = customerMaster.getCustomerAddressLine1() + "," + customerMaster.getCustomerAddressLine2();
            qualityDashboardResponse.setSupplierOrCustomerAddress(customerAddress);
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:SS");
        if (transaction.getVehicleIn() != null) {
            qualityDashboardResponse.setIn(transaction.getVehicleIn().format(formatter));
        }
        if (transaction.getVehicleOut() != null) {
            qualityDashboardResponse.setOut(transaction.getVehicleOut().format(formatter));
        }
        qualityDashboardResponse.setDate(transaction.getTransactionDate());
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
            }
            return reportResponse;
        }
        throw new ResourceNotFoundException("Quality transaction not found for ticketNo: " + ticketNo);
    }
}


