package com.weighbridge.gateuser.services.impl;

import com.weighbridge.SalesManagement.entities.SalesProcess;
import com.weighbridge.SalesManagement.repositories.SalesProcessRepository;
import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.dtos.GateEntryPrint;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.payloads.*;
import com.weighbridge.weighbridgeoperator.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GateEntryTransactionServiceImpl implements GateEntryTransactionService {
    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SiteMasterRepository siteMasterRepository;
    @Autowired
    private CompanyMasterRepository companyMasterRepository;
    @Autowired
    private MaterialMasterRepository materialMasterRepository;
    @Autowired
    private SupplierMasterRepository supplierMasterRepository;
    @Autowired
    private TransporterMasterRepository transporterMasterRepository;
    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;
    @Autowired
    private VehicleTransactionStatusRepository vehicleTransactionStatusRepository;
    @Autowired
    private StatusCodeMasterRepository statusCodeMasterRepository;
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private CustomerMasterRepository customerMasterRepository;
    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private SalesProcessRepository salesProcessRepository;

    @Autowired
    private GateEntryTransactionSpecification gateEntryTransactionSpecification;

    @Autowired
    private QualityTransactionRepository qualityTransactionRepository;


    /**
     * Saves a gate entry transaction based on the provided request data.
     *
     * @param gateEntryTransactionRequest The request data containing gate entry transaction details.
     * @return The ticket number of the saved gate entry transaction.
     * @throws ResourceNotFoundException If the supplier does not exist.
     * @throws ResponseStatusException   If the session is expired and login is required.
     */
    @Override
    public Integer saveGateEntryTransaction(GateEntryTransactionRequest gateEntryTransactionRequest) {
        try {
            // Set user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            String userCompany;
            String userSite;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }

            //getting the name and details of request data
            String materialName = gateEntryTransactionRequest.getMaterial();
            String materialType = gateEntryTransactionRequest.getMaterialType();
            String vehicleNo = gateEntryTransactionRequest.getVehicle();
            String transporterName = gateEntryTransactionRequest.getTransporter();
            String supplierName = gateEntryTransactionRequest.getSupplier();
            String supplierAddress = gateEntryTransactionRequest.getSupplierAddressLine1();
            String customerName = gateEntryTransactionRequest.getCustomer();
            String customerAddress = gateEntryTransactionRequest.getCustomerAddressLine();
            String addressLine1;
            String addressLine2;
            long supplierId = 0;
            long customerId = 0;
            long materialId = 0;

            if (gateEntryTransactionRequest.getTransactionType().equals("Inbound")) {
                if (supplierAddress != null && supplierAddress.contains(",")) {
                    String[] parts = supplierAddress.split(",", 2); // Split into two parts
                    addressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
                    addressLine2 = parts[1].trim();
                } else {
                    // If there's no comma, store everything in supplierAddressLine1
                    addressLine1 = supplierAddress;
                    addressLine2 = null; // Set supplierAddressLine2 to null
                }
                supplierId = supplierMasterRepository.findSupplierIdBySupplierNameAndAddressLines(
                        supplierName, addressLine1, addressLine2);

                if (supplierId == 0) {
                    throw new ResourceNotFoundException("Supplier not exist");
                }
                materialId = materialMasterRepository.findByMaterialIdByMaterialName(materialName);

            }
            if (gateEntryTransactionRequest.getTransactionType().equals("Outbound")) {
                // Outbound transaction
                if (customerAddress != null && customerAddress.contains(",")) {
                    System.out.println("customer " + customerName);
                    String[] parts = customerAddress.split(",", 2); // Split into two parts
                    addressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
                    addressLine2 = parts[1].trim();
                } else {
                    // If there's no comma, store everything in customerAddressLine1
                    addressLine1 = customerAddress;
                    addressLine2 = null; // Set customerAddressLine2 to null
                    System.out.println("================================================================");
                }
                customerId = customerMasterRepository.findCustomerIdByCustomerNameAndAddressLines(customerName, addressLine1, addressLine2);
                System.out.println("custom" + customerId);
                materialId = productMasterRepository.findProductIdByProductName(materialName);
                System.out.println("material" + materialId);
                if (customerId == 0) {
                    throw new ResourceNotFoundException("Customer not exist");
                }
                SalesProcess bySalePassNo = salesProcessRepository.findBySalePassNo(gateEntryTransactionRequest.getTpNo());
                bySalePassNo.setStatus(false);
            }

            //finding the entities by names from database
            long vehicleId = vehicleMasterRepository.findVehicleIdByVehicleNo(vehicleNo);
            long transporterId = transporterMasterRepository.findTransporterIdByTransporterName(transporterName);
            String dlNo = gateEntryTransactionRequest.getDlNo();
            String driverName = gateEntryTransactionRequest.getDriverName();
            Double supplyConsignment = gateEntryTransactionRequest.getSupplyConsignmentWeight();
            String poNo = gateEntryTransactionRequest.getPoNo();
            String tpNo = gateEntryTransactionRequest.getTpNo();
            String challanNo = gateEntryTransactionRequest.getChallanNo();
            String ewaybillNo = gateEntryTransactionRequest.getEwayBillNo();
            //assigin to gateentrytransaction master table
            GateEntryTransaction gateEntryTransaction = new GateEntryTransaction();
            gateEntryTransaction.setTransactionDate(LocalDate.now());
            gateEntryTransaction.setChallanDate(gateEntryTransactionRequest.getChallanDate());
            gateEntryTransaction.setCompanyId(userCompany);
            gateEntryTransaction.setTransporterId(transporterId);
            gateEntryTransaction.setVehicleId(vehicleId);
            gateEntryTransaction.setMaterialId(materialId);
            gateEntryTransaction.setMaterialType(materialType);
            gateEntryTransaction.setSupplierId(supplierId);
            gateEntryTransaction.setSiteId(userSite);
            gateEntryTransaction.setDlNo(dlNo);
            gateEntryTransaction.setDriverName(driverName);
            gateEntryTransaction.setSupplyConsignmentWeight(supplyConsignment);
            gateEntryTransaction.setPoNo(poNo);
            gateEntryTransaction.setTpNo(tpNo);
            gateEntryTransaction.setChallanNo(challanNo);
            gateEntryTransaction.setEwaybillNo(ewaybillNo);
            gateEntryTransaction.setTransactionType(gateEntryTransactionRequest.getTransactionType());
            gateEntryTransaction.setCustomerId(customerId);
            LocalDateTime now = LocalDateTime.now();
            // Round up the seconds
            LocalDateTime vehicleInTime = now.withSecond(0).withNano(0);
            gateEntryTransaction.setVehicleIn(vehicleInTime);
            //save gate entry transaction
            GateEntryTransaction savedGateEntryTransaction = gateEntryTransactionRepository.save(gateEntryTransaction);
            //vehicle transaction status to know where the vehicle is
            VehicleTransactionStatus vehicleTransactionStatus = new VehicleTransactionStatus();
            Integer ticketNo = savedGateEntryTransaction.getTicketNo();
            vehicleTransactionStatus.setTicketNo(ticketNo);
            vehicleTransactionStatus.setStatusCode("GNT");
            vehicleTransactionStatusRepository.save(vehicleTransactionStatus);
            //History save with vehicle intime and vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(ticketNo);
            transactionLog.setTimestamp(vehicleInTime);
            transactionLog.setStatusCode("GNT");
            transactionLogRepository.save(transactionLog);
            return ticketNo;

        } catch (ResponseStatusException ex) {
            // Handle ResponseStatusException
            throw ex;
        } catch (ResourceNotFoundException ex) {
            // Handle ResourceNotFoundException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (Exception ex) {
            // Handle other exceptions
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving gate entry transaction", ex);
        }

    }

    @Override
    public Integer updateGateEntryByTicketNo(GateEntryTransactionRequest gateEntryTransactionRequest, Integer ticketNo) {
        try {

            // Set user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            String userCompany;
            String userSite;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }

            GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);
            if (gateEntryTransaction == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "GateEntry transaction is not available with given ticket No " + ticketNo);
            }

            //getting the name and details of request data
            String materialName = gateEntryTransactionRequest.getMaterial();
            String materialType = gateEntryTransactionRequest.getMaterialType();
            String vehicleNo = gateEntryTransactionRequest.getVehicle();
            String transporterName = gateEntryTransactionRequest.getTransporter();
            String supplierName = gateEntryTransactionRequest.getSupplier();
            String supplierAddress = gateEntryTransactionRequest.getSupplierAddressLine1();
            String customerName = gateEntryTransactionRequest.getCustomer();
            String customerAddress = gateEntryTransactionRequest.getCustomerAddressLine();
            String addressLine1;
            String addressLine2;
            long supplierId = 0;
            long customerId = 0;
            long materialId = 0;

            if (gateEntryTransactionRequest.getTransactionType().equals("Inbound")) {
                if (supplierAddress != null && supplierAddress.contains(",")) {
                    String[] parts = supplierAddress.split(",", 2); // Split into two parts
                    addressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
                    addressLine2 = parts[1].trim();
                } else {
                    // If there's no comma, store everything in supplierAddressLine1
                    addressLine1 = supplierAddress;
                    addressLine2 = null; // Set supplierAddressLine2 to null
                }
                supplierId = supplierMasterRepository.findSupplierIdBySupplierNameAndAddressLines(
                        supplierName, addressLine1, addressLine2);

                if (supplierId == 0) {
                    throw new ResourceNotFoundException("Supplier not exist");
                }
                materialId = materialMasterRepository.findByMaterialIdByMaterialName(materialName);

            }
            if (gateEntryTransactionRequest.getTransactionType().equals("Outbound")) {
                // Outbound transaction
                if (customerAddress != null && customerAddress.contains(",")) {
                    System.out.println("customer " + customerName);
                    String[] parts = customerAddress.split(",", 2); // Split into two parts
                    addressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
                    addressLine2 = parts[1].trim();
                } else {
                    // If there's no comma, store everything in customerAddressLine1
                    addressLine1 = customerAddress;
                    addressLine2 = null; // Set customerAddressLine2 to null
                    System.out.println("================================================================");
                }
                customerId = customerMasterRepository.findCustomerIdByCustomerNameAndAddressLines(customerName, addressLine1, addressLine2);
                System.out.println("custom" + customerId);
                materialId = productMasterRepository.findProductIdByProductName(materialName);
                System.out.println("material" + materialId);
                if (customerId == 0) {
                    throw new ResourceNotFoundException("Customer not exist");
                }
                SalesProcess bySalePassNo = salesProcessRepository.findBySalePassNo(gateEntryTransactionRequest.getTpNo());
                bySalePassNo.setStatus(false);
            }

            //finding the entities by names from database
            long vehicleId = vehicleMasterRepository.findVehicleIdByVehicleNo(vehicleNo);
            long transporterId = transporterMasterRepository.findTransporterIdByTransporterName(transporterName);
            String dlNo = gateEntryTransactionRequest.getDlNo();
            String driverName = gateEntryTransactionRequest.getDriverName();
            Double supplyConsignment = gateEntryTransactionRequest.getSupplyConsignmentWeight();
            String poNo = gateEntryTransactionRequest.getPoNo();
            String tpNo = gateEntryTransactionRequest.getTpNo();
            String challanNo = gateEntryTransactionRequest.getChallanNo();
            String ewaybillNo = gateEntryTransactionRequest.getEwayBillNo();
            //assigin to gateentrytransaction master table
            gateEntryTransaction.setTransactionDate(LocalDate.now());
            gateEntryTransaction.setChallanDate(gateEntryTransactionRequest.getChallanDate());
            gateEntryTransaction.setCompanyId(userCompany);
            gateEntryTransaction.setTransporterId(transporterId);
            gateEntryTransaction.setVehicleId(vehicleId);
            gateEntryTransaction.setMaterialId(materialId);
            gateEntryTransaction.setMaterialType(materialType);
            gateEntryTransaction.setSupplierId(supplierId);
            gateEntryTransaction.setSiteId(userSite);
            gateEntryTransaction.setDlNo(dlNo);
            gateEntryTransaction.setDriverName(driverName);
            gateEntryTransaction.setSupplyConsignmentWeight(supplyConsignment);
            gateEntryTransaction.setPoNo(poNo);
            gateEntryTransaction.setTpNo(tpNo);
            gateEntryTransaction.setChallanNo(challanNo);
            gateEntryTransaction.setEwaybillNo(ewaybillNo);
            gateEntryTransaction.setTransactionType(gateEntryTransactionRequest.getTransactionType());
            gateEntryTransaction.setCustomerId(customerId);
            LocalDateTime now = LocalDateTime.now();
            // Round up the seconds
            LocalDateTime vehicleInTime = now.withSecond(0).withNano(0);
            gateEntryTransaction.setVehicleIn(vehicleInTime);
            //save gate entry transaction
            GateEntryTransaction savedGateEntryTransaction = gateEntryTransactionRepository.save(gateEntryTransaction);
      /*      //vehicle transaction status to know where the vehicle is
            VehicleTransactionStatus vehicleTransactionStatus = new VehicleTransactionStatus();
            vehicleTransactionStatus.setTicketNo(ticketNo);
            vehicleTransactionStatus.setStatusCode("GNT");
            vehicleTransactionStatusRepository.save(vehicleTransactionStatus);*/
            //History save with vehicle intime and vehicle out time
     /*       TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(ticketNo);
            transactionLog.setTimestamp(vehicleInTime);
            transactionLogRepository.save(transactionLog);*/
            return ticketNo;

        } catch (ResponseStatusException ex) {
            // Handle ResponseStatusException
            throw ex;
        } catch (ResourceNotFoundException ex) {
            // Handle ResourceNotFoundException
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        } catch (Exception ex) {
            // Handle other exceptions
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while saving gate entry transaction", ex);
        }
    }

    @Override
    public GateEntryEditResponse editGateEntryByTicketNo(Integer ticketNo) {
        try {

            // Set user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            String userCompany;
            String userSite;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
            GateEntryTransaction transaction = gateEntryTransactionRepository.findByTicketNoAndCompanyIdAndSiteId(ticketNo, userCompany, userSite);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            GateEntryEditResponse response = new GateEntryEditResponse();
            // Fetching associated entity names
            Object[] vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoVehicleNoVehicleTypeFitnessByVehicleId(transaction.getVehicleId());
            String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());

            // Setting values to response object
            response.setTicketNo(String.valueOf(transaction.getTicketNo()));
            response.setTransactionType(transaction.getTransactionType());
            response.setMaterialType(transaction.getMaterialType());
            response.setCompany(transaction.getTransactionType());
            response.setSite(transaction.getSiteId());

            // Check the transaction type and set the appropriate entity
            if ("Inbound".equals(transaction.getTransactionType())) {
                Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
                // Inbound transaction
                Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                if (supplierInfo != null && supplierInfo.length >= 2) {
                    String supplierName = (String) supplierInfo[0];
                    String supplierAddress = (String) supplierInfo[1];
                    response.setSupplier(supplierName);
                    response.setSupplierAddressLine1(supplierAddress);
                }
                String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                response.setMaterial(materialName);
            } else if ("Outbound".equals(transaction.getTransactionType())) {
                Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());
                System.out.println(customerNameByCustomerId);
                // Outbound transaction
                Object[] customerInfo = (Object[]) customerNameByCustomerId[0];
                if (customerInfo != null && customerInfo.length >= 2) {
                    String customerName = (String) customerInfo[0];
                    String customerAddress = (String) customerInfo[1];
                    response.setCustomer(customerName);
                    response.setCustomerAddressLine(customerAddress);
                }
                System.out.println(response);
                String materialName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                response.setMaterial(materialName);
            }
            Object[] vehicleInfo = (Object[]) vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId[0];
            if (vehicleInfo != null && vehicleInfo.length >= 3) {
                String vehicleNo = (String) vehicleInfo[0];
                String vehicleType = (String) vehicleInfo[1];
                Integer vehicleWheelsNo = (Integer) vehicleInfo[2];
                LocalDate vehicleFitnessUpTo = (LocalDate) vehicleInfo[3];
                response.setVehicle(vehicleNo);
                response.setVehicleType(vehicleType);
                response.setVehicleWheelsNo(vehicleWheelsNo);
                response.setVehicleFitnessUpTo(vehicleFitnessUpTo);
            } else {
                // Handle case where vehicle info is not available
                response.setVehicle(null);
            }

            response.setPoNo(transaction.getPoNo());
            response.setChallanNo(transaction.getChallanNo());
            response.setTpNo(transaction.getTpNo());
            response.setSupplyConsignmentWeight(transaction.getSupplyConsignmentWeight());
            response.setTransporter(transporterName);
            response.setChallanDate(transaction.getChallanDate());
            response.setEwayBillNo(transaction.getEwaybillNo());
            response.setDriverName(transaction.getDriverName());
            response.setDlNo(transaction.getDlNo());


           /* QualityTransaction byTicketNo = qualityTransactionRepository.findByTicketNo(transaction.getTicketNo());
            if (byTicketNo != null) {
                response.setQuality(true);
            } else {
                response.setQuality(false);
            }*/
            System.out.println("response " + response);
            return response;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving gate entry transactions. Please try again later.");
        }

    }

    /**
     * Sets the out time for a vehicle based on its ticket number.
     *
     * @param ticketNo The ticket number of the vehicle.
     * @return A message indicating whether the vehicle can exit or not.
     * @throws ResponseStatusException If the session is expired and login is required, or if the vehicle's tare weight is not measured yet.
     */
    @Override
    public String setOutTime(Integer ticketNo) {
        try {
            // Retrieve vehicle transaction status and gate entry transaction
            VehicleTransactionStatus vehicleTransactionStatus = vehicleTransactionStatusRepository.findByTicketNo(ticketNo);
            GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);

            String transactionType = gateEntryTransaction.getTransactionType();
            if (transactionType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction Type is not Mentioned");
            }

            List<String> allowedStatusCodes = gateEntryTransaction.getTransactionType().equalsIgnoreCase("Inbound") ? Arrays.asList("GWT", "TWT") : Arrays.asList("GWT", "QCT");
            System.out.println(allowedStatusCodes);
            List<String> statusCodesByTicket = transactionLogRepository.findStatusCodesByTicket(ticketNo);
            System.out.println(statusCodesByTicket);
            //  System.out.println(statusCodesByTicket.contains(allowedStatusCodes));
            boolean allAllowedStatusCodesPresent = statusCodesByTicket.containsAll(allowedStatusCodes);
            System.out.println("All Allowed Status Codes Present: " + allAllowedStatusCodesPresent);

            if (!allAllowedStatusCodesPresent) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is not allowed to exit!");
            }
            // Retrieve user ID from session
            HttpSession session = httpServletRequest.getSession();
            String userId;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
            // Save transaction log with vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            LocalDateTime vehicleOutTime = LocalDateTime.now().withSecond(0).withNano(0); // Round up seconds
            transactionLog.setTimestamp(vehicleOutTime);
            transactionLog.setTicketNo(ticketNo);
            transactionLog.setStatusCode("GXT");
            transactionLogRepository.save(transactionLog);

            // Update vehicle transaction status and gate entry transaction with out time
            vehicleTransactionStatus.setStatusCode("GXT");
            gateEntryTransaction.setVehicleOut(vehicleOutTime);
            vehicleTransactionStatusRepository.save(vehicleTransactionStatus);

            return "Vehicle can exit";
        } catch (ResponseStatusException ex) {
            // Re-throw ResponseStatusException
            throw ex;
        } catch (Exception ex) {
            // Log the error
            ex.printStackTrace();
            // Return a generic error message
            return "An error occurred while setting out time for the vehicle. Please try again later.";
        }
    }

    @Override
    public GateEntryTransactionPageResponse getAllGateEntryTransaction(Pageable pageable) {
        try {
            // Set user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            String userCompany;
            String userSite;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            Page<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdAndVehicleOutIsNull(pageable, userSite, userCompany);
            System.out.println("GateEntryTransactionServiceImpl.getAllGateEntryTransaction: " + allTransactions);
            Integer totalPage = allTransactions.getTotalPages();
            Long totalElements = allTransactions.getTotalElements();

            List<GateEntryTransactionResponse> responseList = allTransactions.stream()
                    .map(transaction -> {
                        // Fetch status code for the current transaction ticket number
                        VehicleTransactionStatus statusCode = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
                        // Skip processing and printing the transaction if its status code is "GXT"
                        String code = statusCode != null ? statusCode.getStatusCode() : "";
                        if ("GXT".equals(code)) {
                            return null;
                        }

                        GateEntryTransactionResponse response = new GateEntryTransactionResponse();
                        // Fetching associated entity names
                        Object[] vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());
                        String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());

                        // Setting values to response object
                        response.setTicketNo(transaction.getTicketNo());
                        response.setTransactionType(transaction.getTransactionType());
                        response.setMaterialType(transaction.getMaterialType());

                        // Check the transaction type and set the appropriate entity
                        if ("Inbound".equals(transaction.getTransactionType())) {
                            Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
                            // Inbound transaction
                            Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                            if (supplierInfo != null && supplierInfo.length >= 2) {
                                String supplierName = (String) supplierInfo[0];
                                String supplierAddress = (String) supplierInfo[1];
                                response.setSupplier(supplierName);
                                response.setSupplierAddress(supplierAddress);
                            }
                            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                            response.setMaterial(materialName);
                        } else if ("Outbound".equals(transaction.getTransactionType())) {
                            Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());
                            System.out.println(customerNameByCustomerId);
                            // Outbound transaction
                            Object[] customerInfo = (Object[]) customerNameByCustomerId[0];
                            if (customerInfo != null && customerInfo.length >= 2) {
                                String customerName = (String) customerInfo[0];
                                String customerAddress = (String) customerInfo[1];
                                response.setCustomer(customerName);
                                response.setCustomerAddress(customerAddress);
                            }
                            System.out.println(response);
                            String materialName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                            response.setMaterial(materialName);
                        }
                        Object[] vehicleInfo = (Object[]) vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId[0];
                        if (vehicleInfo != null && vehicleInfo.length >= 3) {
                            String vehicleNo = (String) vehicleInfo[0];
                            String vehicleType = (String) vehicleInfo[1];
                            Integer vehicleWheelsNo = (Integer) vehicleInfo[2];
                            response.setVehicleNo(vehicleNo);
                            response.setVehicleType(vehicleType);
                            response.setVehicleWheelsNo(vehicleWheelsNo);
                        } else {
                            // Handle case where vehicle info is not available
                            response.setVehicleNo(null);
                            response.setVehicleType(null);
                            response.setVehicleWheelsNo(null);
                        }
                        // Check if vehicle out transaction log exists
                        if (transaction.getVehicleIn() != null) {
                            // Vehicle out transaction log exists
                            // Process the vehicle out data
                            response.setVehicleIn(transaction.getVehicleIn().format(formatter));
                        }
                        // Check if vehicle out transaction log exists
                        if (transaction.getVehicleOut() != null) {
                            // Vehicle out transaction log exists
                            // Process the vehicle out data
                            response.setVehicleOut(transaction.getVehicleOut().format(formatter));
                        }
                        response.setPoNo(transaction.getPoNo());
                        response.setChallanNo(transaction.getChallanNo());
                        response.setTpNo(transaction.getTpNo());
                        response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
                        response.setTransporter(transporterName);
                        response.setChallanDate(transaction.getChallanDate());
                        response.setTransactionDate(transaction.getTransactionDate());
                        QualityTransaction byTicketNo = qualityTransactionRepository.findByTicketNo(transaction.getTicketNo());
                        if (byTicketNo != null) {
                            response.setQuality(true);
                        } else {
                            response.setQuality(false);
                        }
                        System.out.println("response " + response);
                        return response;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            GateEntryTransactionPageResponse gateEntryTransactionPageResponse = new GateEntryTransactionPageResponse();
            gateEntryTransactionPageResponse.setTransactions(responseList);
            gateEntryTransactionPageResponse.setTotalPages(totalPage);
            gateEntryTransactionPageResponse.setTotalElements(totalElements);
            return gateEntryTransactionPageResponse;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving gate entry transactions. Please try again later.");
        }
    }


    @Override
    public List<GateEntryTransactionResponse> getAllGateEntryTransactionForWeighmentReport(LocalDate startDate, LocalDate endDate) {
        try {
            // Set user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            String userCompany;
            String userSite;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
            if (startDate == null && endDate != null) {
                startDate = endDate;
            }
            if (startDate != null && endDate == null) {
                endDate = startDate;
            }
            LocalDate finalStartDate = startDate;
            LocalDate finalEndDate = endDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");


            List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdAndTransactionDateBetweenOrderByTransactionDateDesc(userSite, userCompany, startDate, endDate);
            System.out.println("GateEntryTransactionServiceImpl.getAllGateEntryTransaction" + allTransactions);
            List<GateEntryTransactionResponse> responseList = new ArrayList<>();
            for (GateEntryTransaction transaction : allTransactions) {
                // Fetch status code for the current transaction ticket number
                VehicleTransactionStatus vehicleTransactionStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
                if (vehicleTransactionStatus != null) {
                    String statusCode = vehicleTransactionStatus.getStatusCode();
                    if ("GXT".equals(statusCode)) {
                        continue;
                    }
                }

                GateEntryTransactionResponse response = new GateEntryTransactionResponse();
                // Fetching associated entity names
                String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                System.out.println("vehicle id" + transaction.getVehicleId());
                System.out.println(" hasg" + vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId()));
                Object[] vehicleNoAndVehicleTypeAndAndvehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());
                System.out.println("vehicle " + vehicleNoAndVehicleTypeAndAndvehicleWheelsNoByVehicleId[0]);
                String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                // Setting values to response object
                response.setTicketNo(transaction.getTicketNo());
                response.setTransactionType(transaction.getTransactionType());
                response.setMaterial(materialName);
                response.setMaterialType(transaction.getMaterialType());

                // Check the transaction type and set the appropriate entity
                if ("Inbound".equals(transaction.getTransactionType())) {
                    Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
                    // Inbound transaction
                    Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                    if (supplierInfo != null && supplierInfo.length >= 2) {
                        String supplierName = (String) supplierInfo[0];
                        String supplierAddress = (String) supplierInfo[1];
                        response.setSupplier(supplierName);
                        response.setSupplierAddress(supplierAddress);
                    }
                } else if ("Outbound".equals(transaction.getTransactionType())) {
                    Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());

                    // Outbound transaction
                    Object[] customerInfo = (Object[]) customerNameByCustomerId[0];
                    if (customerInfo != null && customerInfo.length >= 2) {
                        String customerName = (String) customerInfo[0];
                        String customerAddress = (String) customerInfo[1];
                        response.setCustomer(customerName);
                        response.setCustomerAddress(customerAddress);
                    }
                }

                Object[] vehicleInfo = (Object[]) vehicleNoAndVehicleTypeAndAndvehicleWheelsNoByVehicleId[0];
                if (vehicleInfo != null && vehicleInfo.length >= 3) {
                    String vehicleNo = (String) vehicleInfo[0];
                    String vehicleType = (String) vehicleInfo[1];
                    Integer vehicleWheelsNo = (Integer) vehicleInfo[2];
                    response.setVehicleNo(vehicleNo);
                    response.setVehicleType(vehicleType);
                    response.setVehicleWheelsNo(vehicleWheelsNo);
                } else {
                    // Handle case where vehicle info is not available
                    response.setVehicleNo(null);
                    response.setVehicleType(null);
                    response.setVehicleWheelsNo(null);
                }
                // Check if vehicle out transaction log exists
                if (transaction.getVehicleIn() != null) {
                    // Vehicle out transaction log exists
                    // Process the vehicle out data
                    LocalDateTime vehicleIn = transaction.getVehicleIn();
                    response.setVehicleIn(vehicleIn.format(formatter));

                }
                // Check if vehicle out transaction log exists
                if (transaction.getVehicleOut() != null) {
                    // Vehicle out transaction log exists
                    // Process the vehicle out data
                    response.setVehicleOut(transaction.getVehicleOut().format(formatter));
                }
                response.setPoNo(transaction.getPoNo());
                response.setChallanNo(transaction.getChallanNo());
                response.setTpNo(transaction.getTpNo());
                response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
                response.setTransporter(transporterName);
                response.setChallanDate(transaction.getChallanDate());
                response.setTransactionDate(transaction.getTransactionDate());
                responseList.add(response);
            }
            return responseList;
        } catch (ResponseStatusException ex) {
            // Re-throw ResponseStatusException
            throw ex;
        } catch (Exception ex) {
            // Log the error
            ex.printStackTrace();
            // Return a generic error message
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving gate entry transactions. Please try again later.");
        }
    }

    @Override
    public GateEntryTransactionPageResponse getAllCompletedGateEntry(Pageable pageable) {
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        try {
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            Page<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdAndVehicleOutIsNotNull(pageable, userSite, userCompany);
            Integer totalPage = allTransactions.getTotalPages();
            Long totalElements = allTransactions.getTotalElements();

            List<GateEntryTransactionResponse> responseList = allTransactions.stream()
                    .map(transaction -> {
                        // Fetch status code for the current transaction ticket number
                        String statusCode = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo()).getStatusCode();
                        // Skip processing and printing the transaction if its status code is "GXT"
                   /* if ("GXT".equals(statusCode)) {
                        return null;
                    }*/
                        GateEntryTransactionResponse response = new GateEntryTransactionResponse();
                        // Fetching associated entity names
                        Object[] vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());
                        String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());

                        // Setting values to response object
                        response.setTicketNo(transaction.getTicketNo());
                        response.setTransactionType(transaction.getTransactionType());
                        response.setMaterialType(transaction.getMaterialType());

                        // Check the transaction type and set the appropriate entity
                        if ("Inbound".equals(transaction.getTransactionType())) {
                            Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
                            // Inbound transaction
                            Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                            if (supplierInfo != null && supplierInfo.length >= 2) {
                                String supplierName = (String) supplierInfo[0];
                                String supplierAddress = (String) supplierInfo[1];
                                response.setSupplier(supplierName);
                                response.setSupplierAddress(supplierAddress);
                            }
                            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                            response.setMaterial(materialName);
                        } else if ("Outbound".equals(transaction.getTransactionType())) {
                            Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());
                            System.out.println(customerNameByCustomerId);
                            // Outbound transaction
                            Object[] customerInfo = (Object[]) customerNameByCustomerId[0];
                            if (customerInfo != null && customerInfo.length >= 2) {
                                String customerName = (String) customerInfo[0];
                                String customerAddress = (String) customerInfo[1];
                                response.setCustomer(customerName);
                                response.setCustomerAddress(customerAddress);
                            }
                            String materialName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                            response.setMaterial(materialName);
                        }
                        Object[] vehicleInfo = (Object[]) vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId[0];
                        if (vehicleInfo != null && vehicleInfo.length >= 3) {
                            String vehicleNo = (String) vehicleInfo[0];
                            String vehicleType = (String) vehicleInfo[1];
                            Integer vehicleWheelsNo = (Integer) vehicleInfo[2];
                            response.setVehicleNo(vehicleNo);
                            response.setVehicleType(vehicleType);
                            response.setVehicleWheelsNo(vehicleWheelsNo);
                        } else {
                            // Handle case where vehicle info is not available
                            response.setVehicleNo(null);
                            response.setVehicleType(null);
                            response.setVehicleWheelsNo(null);
                        }
                        // Check if vehicle out transaction log exists
                        if (transaction.getVehicleIn() != null) {
                            // Vehicle out transaction log exists
                            // Process the vehicle out data
                            response.setVehicleIn(transaction.getVehicleIn().format(formatter));
                        }
                        // Check if vehicle out transaction log exists
                        if (transaction.getVehicleOut() != null) {
                            // Vehicle out transaction log exists
                            // Process the vehicle out data
                            response.setVehicleOut(transaction.getVehicleOut().format(formatter));
                        }
                        response.setPoNo(transaction.getPoNo());
                        response.setChallanNo(transaction.getChallanNo());
                        response.setTpNo(transaction.getTpNo());
                        response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
                        response.setTransporter(transporterName);
                        response.setChallanDate(transaction.getChallanDate());
                        response.setTransactionDate(transaction.getTransactionDate());
                        System.out.println("response " + response);
                        return response;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            GateEntryTransactionPageResponse gateEntryTransactionPageResponse = new GateEntryTransactionPageResponse();
            gateEntryTransactionPageResponse.setTransactions(responseList);
            gateEntryTransactionPageResponse.setTotalPages(totalPage);
            gateEntryTransactionPageResponse.setTotalElements(totalElements);
            return gateEntryTransactionPageResponse;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving gate entry transactions. Please try again later.");
        }

    }


    @Override
    public GateEntryTransactionPageResponse findTransactionsByFiltering(Integer ticketNo, String vehicleNo, LocalDate date, String supplierNameC, String transactionType, Pageable pageable, String vehicleStatus) {
        try {
            // Set user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            String userCompany;
            String userSite;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
                userSite = session.getAttribute("userSite").toString();
                userCompany = session.getAttribute("userCompany").toString();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
            }
            Page<GateEntryTransaction> gateEntryTransactionPage = gateEntryTransactionRepository.findAll(gateEntryTransactionSpecification.getTransactions(ticketNo, vehicleNo, date, supplierNameC, transactionType, vehicleStatus)
                    .and(gateEntryTransactionSpecification.filterBySiteAndCompany(userSite, userCompany)), pageable);
            // Convert Page content to List
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            List<GateEntryTransactionResponse> transactionResponses = gateEntryTransactionPage.getContent().stream()
                    .map(transaction -> {
                        // Set all fields from transaction to response
                        GateEntryTransactionResponse response = new GateEntryTransactionResponse();
                        // Fetching associated entity names
                        Object[] vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());
                        String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());

                        // Setting values to response object
                        response.setTicketNo(transaction.getTicketNo());
                        response.setTransactionType(transaction.getTransactionType());
                        response.setMaterialType(transaction.getMaterialType());

                        // Check the transaction type and set the appropriate entity
                        if ("Inbound".equals(transaction.getTransactionType())) {
                            Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
                            // Inbound transaction
                            Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                            if (supplierInfo != null && supplierInfo.length >= 2) {
                                String supplierName = (String) supplierInfo[0];
                                String supplierAddress = (String) supplierInfo[1];
                                response.setSupplier(supplierName);
                                response.setSupplierAddress(supplierAddress);
                            }
                            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                            response.setMaterial(materialName);
                        } else if ("Outbound".equals(transaction.getTransactionType())) {
                            Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());
                            // Outbound transaction
                            Object[] customerInfo = (Object[]) customerNameByCustomerId[0];
                            if (customerInfo != null && customerInfo.length >= 2) {
                                String customerName = (String) customerInfo[0];
                                String customerAddress = (String) customerInfo[1];
                                response.setCustomer(customerName);
                                response.setCustomerAddress(customerAddress);
                            }
                            String materialName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                            response.setMaterial(materialName);
                        }
                        Object[] vehicleInfo = (Object[]) vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId[0];
                        if (vehicleInfo != null && vehicleInfo.length >= 3) {
                            String vehicleNoGet = (String) vehicleInfo[0];
                            String vehicleType = (String) vehicleInfo[1];
                            Integer vehicleWheelsNo = (Integer) vehicleInfo[2];
                            response.setVehicleNo(vehicleNoGet);
                            response.setVehicleType(vehicleType);
                            response.setVehicleWheelsNo(vehicleWheelsNo);
                        } else {
                            // Handle case where vehicle info is not available
                            response.setVehicleNo(null);
                            response.setVehicleType(null);
                            response.setVehicleWheelsNo(null);
                        }
                        // Check if vehicle out transaction log exists
                        if (transaction.getVehicleIn() != null) {
                            // Vehicle out transaction log exists
                            // Process the vehicle out data
                            response.setVehicleIn(transaction.getVehicleIn().format(formatter));
                        }
                        // Check if vehicle out transaction log exists
                        if (transaction.getVehicleOut() != null) {
                            // Vehicle out transaction log exists
                            // Process the vehicle out data
                            response.setVehicleOut(transaction.getVehicleOut().format(formatter));
                        }
                        response.setPoNo(transaction.getPoNo());
                        response.setChallanNo(transaction.getChallanNo());
                        response.setTpNo(transaction.getTpNo());
                        response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
                        response.setTransporter(transporterName);
                        response.setChallanDate(transaction.getChallanDate());
                        response.setTransactionDate(transaction.getTransactionDate());
                        System.out.println("response " + response);
                        return response;
                    })
                    .collect(Collectors.toList());

            // Populate the custom response DTO
            GateEntryTransactionPageResponse gateEntryTransactionPageResponse = new GateEntryTransactionPageResponse();
            gateEntryTransactionPageResponse.setTransactions(transactionResponses);
            gateEntryTransactionPageResponse.setTotalPages(gateEntryTransactionPage.getTotalPages());
            gateEntryTransactionPageResponse.setTotalElements(gateEntryTransactionPage.getTotalElements());
            return gateEntryTransactionPageResponse;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while retrieving gate entry transactions. Please try again later.");
        }
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

    /**
     * @param ticketNo
     * @return
     */
    @Override
    public GateEntryPrint getPrintTicketWise(Integer ticketNo) {
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        GateEntryPrint gateEntryPrint = new GateEntryPrint();
        if (gateEntryTransaction != null) {
            gateEntryPrint.setTicketNo(ticketNo);
            gateEntryPrint.setTransactionType(gateEntryTransaction.getTransactionType());
            gateEntryPrint.setChallanDate(gateEntryTransaction.getChallanDate() != null ? String.valueOf(gateEntryTransaction.getChallanDate()) : "");
            gateEntryPrint.setChallanNo(gateEntryTransaction.getChallanNo());
            gateEntryPrint.setPoNo(gateEntryTransaction.getPoNo());
            gateEntryPrint.setTpNetWeight(gateEntryTransaction.getSupplyConsignmentWeight());
            gateEntryPrint.setTpNo(gateEntryTransaction.getTpNo());
            gateEntryPrint.setTransactionDate(gateEntryTransaction.getTransactionDate() != null ? String.valueOf(gateEntryTransaction.getTransactionDate()) : "");
            gateEntryPrint.setVehicleNo(vehicleMasterRepository.findVehicleNoById(gateEntryTransaction.getVehicleId()));
            gateEntryPrint.setVehicleIn(gateEntryTransaction.getVehicleIn() != null ? gateEntryTransaction.getVehicleIn().format(formatter) : "");
            gateEntryPrint.setVehicleOut(gateEntryTransaction.getVehicleOut() != null ? gateEntryTransaction.getVehicleOut().format(formatter) : "");
            gateEntryPrint.setTransporter(transporterMasterRepository.findTransporterNameByTransporterId(gateEntryTransaction.getTransporterId()));
            if (gateEntryTransaction.getTransactionType().equalsIgnoreCase("Inbound")) {
                gateEntryPrint.setMaterial(materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId()));
                Object[] supplierNameAndAddressBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(gateEntryTransaction.getSupplierId());
                Object[] supplierInfo = (Object[]) supplierNameAndAddressBySupplierId[0];
                if (supplierInfo != null && supplierInfo.length >= 2) {
                    String supplierName = (String) supplierInfo[0];
                    String supplierAddress = (String) supplierInfo[1];
                    gateEntryPrint.setSupplier(supplierName);
                    gateEntryPrint.setSupplierAddress(supplierAddress);
                }
                gateEntryPrint.setMaterialType(gateEntryTransaction.getMaterialType());
            } else {
                gateEntryPrint.setProductName(productMasterRepository.findProductNameByProductId(gateEntryTransaction.getMaterialId()));
                gateEntryPrint.setProductType(gateEntryPrint.getMaterialType());
                Object[] customerNameAndAddressBycustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(gateEntryTransaction.getCustomerId());
                Object[] customerInfo = (Object[]) customerNameAndAddressBycustomerId[0];
                if (customerInfo != null && customerInfo.length >= 2) {
                    String customerName = (String) customerInfo[0];
                    String customerAddress = (String) customerInfo[1];
                    gateEntryPrint.setCustomer(customerName);
                    gateEntryPrint.setCustomerAddress(customerAddress);
                }
            }
            gateEntryPrint.setCompanyName(companyMasterRepository.findCompanyNameByCompanyId(gateEntryTransaction.getCompanyId()));
            gateEntryPrint.setSiteName(siteMasterRepository.findSiteNameBySiteId(gateEntryTransaction.getSiteId()));
        }
        return gateEntryPrint;
    }


    @Override
    public Long countPendingGateTransactionsInbound() {
        Long count = gateEntryTransactionRepository.countPendingGateTransactionsInbound();
        if (count != null) {
            return count;
        }
        return 0L;
    }

    @Override
    public Long countPendingGateTransactionsOutbound() {
        Long count = gateEntryTransactionRepository.countPendingGateTransactionsOutbound();
        if (count != null) {
            return count;
        }
        return 0L;
    }

    @Override
    public Long countCompleteTransactions() {
        Long count = gateEntryTransactionRepository.countCompleteGateTransaction();
        if (count != null) {
            return count;
        }
        return 0L;
    }
}
