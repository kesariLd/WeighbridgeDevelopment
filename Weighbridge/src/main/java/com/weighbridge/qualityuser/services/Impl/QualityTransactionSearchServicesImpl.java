package com.weighbridge.qualityuser.services.Impl;

import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.entities.SupplierMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.payloads.QualityDashboardResponse;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.qualityuser.services.QualityTransactionSearchService;
import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

    public QualityTransactionSearchServicesImpl(QualityTransactionRepository qualityTransactionRepository, GateEntryTransactionRepository gateEntryTransactionRepository, HttpServletRequest httpServletRequest, VehicleTransactionStatusRepository vehicleTransactionStatusRepository, SupplierMasterRepository supplierMasterRepository, CustomerMasterRepository customerMasterRepository, MaterialMasterRepository materialMasterRepository, TransporterMasterRepository transporterMasterRepository, VehicleMasterRepository vehicleMasterRepository, TransactionLogRepository transactionLogRepository, QualityRangeMasterRepository qualityRangeMasterRepository, CompanyMasterRepository companyMasterRepository, ProductMasterRepository productMasterRepository) {
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

    //search by ticketNo
    @Override
    public QualityDashboardResponse searchByTicketNo(Integer ticketNo) {
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompanyId;
        String userSiteId;

        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSiteId = session.getAttribute("userSite").toString();
            userCompanyId = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again!");
        }
        QualityDashboardResponse qualityDashboardResponse = null;

        if (ticketNo != null) {
            GateEntryTransaction transactionByTicketNo = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(ticketNo, userCompanyId, userSiteId);
            if (transactionByTicketNo != null) {
                TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(ticketNo, "QCT");
                if (qctTransactionLog != null) {
                    throw new ResponseStatusException(HttpStatus.FOUND, "Quality is exist for the ticket no : " + ticketNo);
                }
                qualityDashboardResponse = new QualityDashboardResponse();
                setQualityDashboardResponseDetails(qualityDashboardResponse, transactionByTicketNo);
            } else {
                // Throw an exception if transactionByTicketNo is null
                throw new ResourceNotFoundException("Ticket", "ticket no", String.valueOf(ticketNo));
            }
        }

        return qualityDashboardResponse;
    }

    //search by supplierName and address
    @Override
    public List<QualityDashboardResponse> searchBySupplierOrCustomerNameAndAddress(String supplierOrCustomerName, String supplierOrCustomerAddress) {
        List<QualityDashboardResponse> responses = new ArrayList<>();
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompanyId;
        String userSiteId;

        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSiteId = session.getAttribute("userSite").toString();
            userCompanyId = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        // Search for supplierName and supplierAddress
        if (supplierOrCustomerName != null || supplierOrCustomerAddress != null) {
            System.out.println("supplierOrCustomer Name:" + supplierOrCustomerName);
            System.out.println("supplierOrCustomer address :" + supplierOrCustomerAddress);

            List<SupplierMaster> supplierMasters = supplierMasterRepository.findBySupplierNameContainingOrSupplierAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
            List<CustomerMaster> customerMasters = new ArrayList<>();
            log.info("Supplier master is null",  supplierMasters.isEmpty());
            if (supplierMasters.isEmpty()) {
              customerMasters = customerMasterRepository.findByCustomerNameContainingOrCustomerAddressLine1Containing(supplierOrCustomerName, supplierOrCustomerAddress);
                System.out.println("number of customers :" + customerMasters.size());
            }

            List<GateEntryTransaction> transactions = new ArrayList<>();

            if (!supplierMasters.isEmpty()) {
                for (SupplierMaster supplierMaster : supplierMasters) {
                    List<GateEntryTransaction> gateEntryTransaction = gateEntryTransactionRepository.findBySupplierIdOrderByTicketNoDesc(supplierMaster.getSupplierId());
                    transactions.addAll(gateEntryTransaction);
                    System.out.println("Number of transactions for supplier " + supplierMaster.getSupplierId() + ": " + gateEntryTransaction.size());
                }
            }

            if (!customerMasters.isEmpty()) {
                for (CustomerMaster customerMaster : customerMasters) {
                    List<GateEntryTransaction> gateEntryTransaction = gateEntryTransactionRepository.findByCustomerIdOrderByTicketNoDesc(customerMaster.getCustomerId());
                    transactions.addAll(gateEntryTransaction);
                    System.out.println("Number of transactions for customer " + customerMaster.getCustomerId() + ": " + gateEntryTransaction.size());
                }
            }

            for (GateEntryTransaction transaction : transactions) {
                System.out.println("Transaction with ticket no: " + transaction.getTicketNo() + ", CompanyId: " + userCompanyId + ", SiteId: " + userSiteId);

                // Check if the transaction is valid based on the current user context
                GateEntryTransaction transactionBySupplierOrCustomerNameAndAddress = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(transaction.getTicketNo(), userCompanyId, userSiteId);
                if (transactionBySupplierOrCustomerNameAndAddress != null) {
                    System.out.println("Found transaction: " + transactionBySupplierOrCustomerNameAndAddress.getTicketNo());

                    TransactionLog qctTransactionLog = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "QCT");
                    if (qctTransactionLog == null) {
                        QualityDashboardResponse qualityDashboardResponse = new QualityDashboardResponse();
                        setQualityDashboardResponseDetails(qualityDashboardResponse, transactionBySupplierOrCustomerNameAndAddress);
                        responses.add(qualityDashboardResponse);
                        System.out.println("Added quality response for ticket no: " + transaction.getTicketNo());
                    } else {
                        System.out.println("QCT log already exists for ticket no: " + transaction.getTicketNo());
                    }
                } else {
                    System.out.println("No transaction found for ticket no: " + transaction.getTicketNo() + ", CompanyId: " + userCompanyId + ", SiteId: " + userSiteId);
                }
            }
        } else {
            System.out.println("Supplier/Customer name and address are both null.");
        }
        return responses;
    }

    @Override
    public List<QualityDashboardResponse> searchByDate(String date) {
        List<QualityDashboardResponse> responses = new ArrayList<>();
        try {
            HttpSession session = httpServletRequest.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                throw new SessionExpiredException("Session Expired, Login again!");
            }
            String userId = session.getAttribute("userId").toString();
            String userSite = session.getAttribute("userSite").toString();
            String userCompany = session.getAttribute("userCompany").toString();

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
        } catch (SessionExpiredException e) {
            log.error("Session expired: ", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired, login again");
        } catch (Exception e) {
            log.error("Error occurred while searching: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while searching");
        }
        return responses;
    }

    @Override
    public List<QualityDashboardResponse> searchByVehicleNo(String vehicleNo) {
        List<QualityDashboardResponse> responses = new ArrayList<>();
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompanyId;
        String userSiteId;

        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSiteId = session.getAttribute("userSite").toString();
            userCompanyId = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again!");
        }

        // Search by vehicleNo
        if (vehicleNo != null) {
            VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(vehicleNo);
            if (vehicleMaster != null) {
                List<GateEntryTransaction> transactionsByVehicleId = gateEntryTransactionRepository.findByVehicleIdOrderByTicketNo(vehicleMaster.getId());
                for (GateEntryTransaction gateEntryTransaction : transactionsByVehicleId) {
                    GateEntryTransaction transactionByTicketNo = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(gateEntryTransaction.getTicketNo(), userCompanyId, userSiteId);
                    if (transactionByTicketNo != null) {
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



}
