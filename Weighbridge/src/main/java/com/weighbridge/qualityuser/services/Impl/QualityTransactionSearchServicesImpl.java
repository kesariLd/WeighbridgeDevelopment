package com.weighbridge.qualityuser.services.Impl;

import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.entities.SupplierMaster;
import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.ProductMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.UserMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.qualityuser.services.QualityTransactionSearchService;
import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class QualityTransactionSearchServicesImpl implements QualityTransactionSearchService {

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
    private final UserMasterRepository userMasterRepository;

    public QualityTransactionSearchServicesImpl(QualityTransactionRepository qualityTransactionRepository, GateEntryTransactionRepository gateEntryTransactionRepository, HttpServletRequest httpServletRequest, VehicleTransactionStatusRepository vehicleTransactionStatusRepository, SupplierMasterRepository supplierMasterRepository, CustomerMasterRepository customerMasterRepository, MaterialMasterRepository materialMasterRepository, TransporterMasterRepository transporterMasterRepository, VehicleMasterRepository vehicleMasterRepository, TransactionLogRepository transactionLogRepository, QualityRangeMasterRepository qualityRangeMasterRepository, CompanyMasterRepository companyMasterRepository, ProductMasterRepository productMasterRepository, UserMasterRepository userMasterRepository) {
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
        this.userMasterRepository = userMasterRepository;
    }

    @Override
    public QualityDashboardResponse searchByTicketNo(Integer ticketNo, String userId, boolean checkQualityCompleted) {
        UserMaster userMaster = Optional.ofNullable(userMasterRepository.findByUserId(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session timed out, Login again!"));

        String userSite = userMaster.getSite().getSiteId();
        String userCompany = userMaster.getCompany().getCompanyId();
        if (ticketNo == null) {
            throw new IllegalArgumentException("Ticket number cannot be null");
        }

        GateEntryTransaction transactionByTicketNo = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(ticketNo, userCompany, userSite);
        if (transactionByTicketNo == null) {
            throw new ResourceNotFoundException("Ticket", "ticket no", String.valueOf(ticketNo));
        }

        TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(ticketNo, "QCT");
        if (checkQualityCompleted && qctTransactionLog == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quality is not completed for the ticket no: " + ticketNo);
        } else if (!checkQualityCompleted && qctTransactionLog != null) {
            throw new ResponseStatusException(HttpStatus.FOUND, "Quality is completed for the ticket no: " + ticketNo);
        }
//        if (qctTransactionLog == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quality is not completed for the ticket no: " + ticketNo);
//        }
        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
        setQualityDashboardResponseDetails(qualityDashboardResponse, transactionByTicketNo);
        return qualityDashboardResponse;
    }

    //search by supplierName and address
    @Override
public List<QualityDashboardResponse> searchBySupplierOrCustomerNameAndAddress(String supplierOrCustomerName, String supplierOrCustomerAddress, String userId) {
    UserMaster userMaster = Optional.ofNullable(userMasterRepository.findByUserId(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session timed out, Login again!"));

    String userSite = userMaster.getSite().getSiteId();
    String userCompany = userMaster.getCompany().getCompanyId();

    List<QualityDashboardResponse> responses = new ArrayList<>();

    // Search for supplierName and supplierAddress
    if (supplierOrCustomerName != null || supplierOrCustomerAddress != null) {
        List<SupplierMaster> supplierMasters = supplierMasterRepository.findBySupplierNameContainingOrSupplierAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
        List<CustomerMaster> customerMasters = new ArrayList<>();

        if (supplierMasters.isEmpty()) {
            customerMasters = customerMasterRepository.findByCustomerNameContainingOrCustomerAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
        }

        List<GateEntryTransaction> transactions = new ArrayList<>();

        if (!supplierMasters.isEmpty()) {
            for (SupplierMaster supplierMaster : supplierMasters) {
                List<GateEntryTransaction> gateEntryTransaction = gateEntryTransactionRepository.findBySupplierIdOrderByTicketNoDesc(supplierMaster.getSupplierId());
                transactions.addAll(gateEntryTransaction);
            }
        }

        if (!customerMasters.isEmpty()) {
            for (CustomerMaster customerMaster : customerMasters) {
                List<GateEntryTransaction> gateEntryTransaction = gateEntryTransactionRepository.findByCustomerIdOrderByTicketNoDesc(customerMaster.getCustomerId());
                transactions.addAll(gateEntryTransaction);
            }
        }

        for (GateEntryTransaction transaction : transactions) {
            // Check if the transaction is valid based on the current user context
            GateEntryTransaction transactionBySupplierOrCustomerNameAndAddress = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(transaction.getTicketNo(), userCompany, userSite);
            if (transactionBySupplierOrCustomerNameAndAddress != null) {
                // Determine the status codes for GWT and TWT
                String gwtStatusCode = "GWT";
                String twtStatusCode = "TWT";

                // Check for completed GWT or TWT based on transaction type
                boolean isCompleted = false;
                if (transactionBySupplierOrCustomerNameAndAddress.getTransactionType().equalsIgnoreCase("Inbound")) {
                    TransactionLog gwtTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), gwtStatusCode);
                    isCompleted = (gwtTransactionLog != null);
                } else if (transactionBySupplierOrCustomerNameAndAddress.getTransactionType().equalsIgnoreCase("Outbound")) {
                    TransactionLog twtTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), twtStatusCode);
                    isCompleted = (twtTransactionLog != null);
                }

                if (isCompleted) {
                    TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transactionBySupplierOrCustomerNameAndAddress.getTicketNo(), "QCT");
                    if (qctTransactionLog == null) {
                        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                        setQualityDashboardResponseDetails(qualityDashboardResponse, transactionBySupplierOrCustomerNameAndAddress);
                        responses.add(qualityDashboardResponse);
                    }
                }
            }
        }
    }
    return responses;
}

    @Override
public List<QualityDashboardResponse> searchBySupplierOrCustomerNameAndAddressQctCompleted(String supplierOrCustomerName, String supplierOrCustomerAddress, String userId) {
    UserMaster userMaster = Optional.ofNullable(userMasterRepository.findByUserId(userId))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session timed out, Login again!"));

    String userSite = userMaster.getSite().getSiteId();
    String userCompany = userMaster.getCompany().getCompanyId();

    List<QualityDashboardResponse> responses = new ArrayList<>();

    // Search for supplierName and supplierAddress
    if (supplierOrCustomerName != null || supplierOrCustomerAddress != null) {
        List<SupplierMaster> supplierMasters = supplierMasterRepository.findBySupplierNameContainingOrSupplierAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
        List<CustomerMaster> customerMasters = new ArrayList<>();

        if (supplierMasters.isEmpty()) {
            customerMasters = customerMasterRepository.findByCustomerNameContainingOrCustomerAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
        }

        List<GateEntryTransaction> transactions = new ArrayList<>();

        if (!supplierMasters.isEmpty()) {
            for (SupplierMaster supplierMaster : supplierMasters) {
                List<GateEntryTransaction> gateEntryTransaction = gateEntryTransactionRepository.findBySupplierIdOrderByTicketNoDesc(supplierMaster.getSupplierId());
                transactions.addAll(gateEntryTransaction);
            }
        }

        if (!customerMasters.isEmpty()) {
            for (CustomerMaster customerMaster : customerMasters) {
                List<GateEntryTransaction> gateEntryTransaction = gateEntryTransactionRepository.findByCustomerIdOrderByTicketNoDesc(customerMaster.getCustomerId());
                transactions.addAll(gateEntryTransaction);
            }
        }

        for (GateEntryTransaction transaction : transactions) {
            // Check if the transaction is valid based on the current user context
            GateEntryTransaction transactionBySupplierOrCustomerNameAndAddress = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(transaction.getTicketNo(), userCompany, userSite);
            if (transactionBySupplierOrCustomerNameAndAddress != null) {
                TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                if (qctTransactionLog != null) {
                    QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                    setQualityDashboardResponseDetails(qualityDashboardResponse, transactionBySupplierOrCustomerNameAndAddress);
                    responses.add(qualityDashboardResponse);
                }
            }
        }
    }
    return responses;
}

    @Override
    public List<QualityDashboardResponse> searchByDate(String date, String userId) {
        List<QualityDashboardResponse> responses = new ArrayList<>();
        try {
            UserMaster userMaster = Optional.ofNullable(userMasterRepository.findByUserId(userId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session timed out, Login again!"));

            String userSite = userMaster.getSite().getSiteId();
            String userCompany = userMaster.getCompany().getCompanyId();

            LocalDate searchDate = LocalDate.parse(date);

            // Retrieve transactions for the user's site and company, ordered by transaction date
            List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTransactionDateDesc(userSite, userCompany);
            for (GateEntryTransaction transaction : allTransactions) {
                if (transaction.getTransactionDate().isEqual(searchDate)) {
                    String statusCode = transaction.getTransactionType().equalsIgnoreCase("Inbound") ? "GWT" : "TWT";
                    TransactionLog transactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), statusCode);
                    if (transactionLog != null) {
                        TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                        if (qctTransactionLog == null) {
                            QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                            setQualityDashboardResponseDetails(qualityDashboardResponse, transaction);
                            responses.add(qualityDashboardResponse);
                        }
                    }
                }
            }
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: ", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        } catch (Exception e) {
            log.error("Error occurred while searching: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while searching");
        }
        return responses;
    }

    @Override
    public List<QualityDashboardResponse> searchByVehicleNo(String vehicleNo, String userId) {
        UserMaster userMaster = Optional.ofNullable(userMasterRepository.findByUserId(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session timed out, Login again!"));

        String userSite = userMaster.getSite().getSiteId();
        String userCompany = userMaster.getCompany().getCompanyId();

        List<QualityDashboardResponse> responses = new ArrayList<>();
        // Search by vehicleNo
        if (vehicleNo != null) {
            VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);
            if (vehicleMaster != null) {
                List<GateEntryTransaction> transactionsByVehicleId = gateEntryTransactionRepository.findByVehicleIdOrderByTicketNo(vehicleMaster.getId());
                for (GateEntryTransaction gateEntryTransaction : transactionsByVehicleId) {
                    GateEntryTransaction transactionByTicketNo = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(gateEntryTransaction.getTicketNo(), userCompany, userSite);
                    if (transactionByTicketNo != null) {
                        // Determine the status code based on transaction type
                        String inboundStatusCode = "GWT";
                        String outboundStatusCode = "TWT";

                        boolean isInboundComplete = false;
                        boolean isOutboundComplete = false;

                        if (transactionByTicketNo.getTransactionType().equalsIgnoreCase("Inbound")) {
                            TransactionLog inboundLog = transactionLogRepository.findByTicketNoAndStatusCode(transactionByTicketNo.getTicketNo(), inboundStatusCode);
                            isInboundComplete = (inboundLog != null);
                        } else if (transactionByTicketNo.getTransactionType().equalsIgnoreCase("Outbound")) {
                            TransactionLog outboundLog = transactionLogRepository.findByTicketNoAndStatusCode(transactionByTicketNo.getTicketNo(), outboundStatusCode);
                            isOutboundComplete = (outboundLog != null);
                        }

                        if ((transactionByTicketNo.getTransactionType().equalsIgnoreCase("Inbound") && isInboundComplete) ||
                                (transactionByTicketNo.getTransactionType().equalsIgnoreCase("Outbound") && isOutboundComplete)) {
                            TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transactionByTicketNo.getTicketNo(), "QCT");
                            if (qctTransactionLog == null) {
                                QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                                setQualityDashboardResponseDetails(qualityDashboardResponse, gateEntryTransaction);
                                responses.add(qualityDashboardResponse);
                            }
                        }
                    }
                }
            }
        }
        return responses;
    }


    @Override
    public List<QualityDashboardResponse> searchByQCTCompletedVehicleNo(String vehicleNo, String userId) {
         UserMaster userMaster = Optional.ofNullable(userMasterRepository.findByUserId(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session timed out, Login again!"));

        String userSite = userMaster.getSite().getSiteId();
        String userCompany = userMaster.getCompany().getCompanyId();
        
        List<QualityDashboardResponse> responses = new ArrayList<>();
        
        // Search by vehicleNo
        if (vehicleNo != null) {
            VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);
            if (vehicleMaster != null) {
                List<GateEntryTransaction> transactionsByVehicleId = gateEntryTransactionRepository.findByVehicleIdOrderByTicketNo(vehicleMaster.getId());
                for (GateEntryTransaction gateEntryTransaction : transactionsByVehicleId) {
                    GateEntryTransaction transactionByTicketNo = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(gateEntryTransaction.getTicketNo(), userCompany, userSite);
                    if (transactionByTicketNo != null) {
                        TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transactionByTicketNo.getTicketNo(), "QCT");
                        if (qctTransactionLog != null) {
                            QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                            setQualityDashboardResponseDetails(qualityDashboardResponse, gateEntryTransaction);
                            responses.add(qualityDashboardResponse);
                        }

                    }
                }
            }
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
        QualityTransaction qualityTransaction = qualityTransactionRepository.findByTicketNo(transaction.getTicketNo());
        if (qualityTransaction == null) {
            qualityDashboardResponse.setQualityParametersPresent(false);
        } else {
            qualityDashboardResponse.setQualityParametersPresent(true);
        }


    }


}
