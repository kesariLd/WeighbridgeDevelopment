package com.weighbridge.gateuser.services.impl;
import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.dtos.GateEntryTransactionDto;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.payloads.GateEntryTransactionRequest;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

    /**
     * Saves a gate entry transaction based on the provided request data.
     *
     * @param gateEntryTransactionRequest The request data containing gate entry transaction details.
     * @return The ticket number of the saved gate entry transaction.
     * @throws ResourceNotFoundException If the supplier does not exist.
     * @throws ResponseStatusException If the session is expired and login is required.
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
            } else { // Outbound transaction
                if (customerAddress != null && customerAddress.contains(",")) {
                    String[] parts = customerAddress.split(",", 2); // Split into two parts
                    addressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
                    addressLine2 = parts[1].trim();
                } else {
                    // If there's no comma, store everything in customerAddressLine1
                    addressLine1 = customerAddress;
                    addressLine2 = null; // Set customerAddressLine2 to null
                }
                customerId = customerMasterRepository.findCustomerIdByCustomerNameAndAddressLines(customerName,addressLine1,addressLine2);
                if (customerId == 0) {
                    throw new ResourceNotFoundException("Customer not exist");
                }
            }

            //finding the entities by names from database
            long materialId = materialMasterRepository.findByMaterialIdByMaterialName(materialName);
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
            if(transactionType==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Transaction Type is not Mentioned");
            }

            List<String> allowedStatusCodes = gateEntryTransaction.getTransactionType().equalsIgnoreCase("Inbound") ? Arrays.asList("TWT", "QCK") : Arrays.asList("GWT", "QCK");

            if (!allowedStatusCodes.contains(vehicleTransactionStatus.getStatusCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is not measured yet!");
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
    // todo Inbound supplier show , outbound Customer show
    @Override
    public List<GateEntryTransactionResponse> getAllGateEntryTransaction() {
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

            List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTicketNoDesc(userSite, userCompany);
            System.out.println("GateEntryTransactionServiceImpl.getAllGateEntryTransaction" + allTransactions);
            List<GateEntryTransactionResponse> responseList = new ArrayList<>();
            for (GateEntryTransaction transaction : allTransactions) {
                // Fetch status code for the current transaction ticket number
                String statusCode = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo()).getStatusCode();
                // Skip processing and printing the transaction if its status code is "GXT"
                if ("GXT".equals(statusCode)) {
                    continue;
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
                    Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameBySupplierId(transaction.getSupplierId());
                    // Inbound transaction
                    Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                    if (supplierInfo != null && supplierInfo.length >= 2) {
                        String supplierName = (String) supplierInfo[0];
                        String supplierAddress = (String) supplierInfo[1];
                        response.setSupplier(supplierName);
                        response.setSupplierAddress(supplierAddress);
                    }
                } else if ("Outbound".equals(transaction.getTransactionType())) {
                    Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameBycustomerId(transaction.getCustomerId());

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
                    response.setVehicleIn(transaction.getVehicleIn());
                }
                // Check if vehicle out transaction log exists
                if (transaction.getVehicleOut() != null) {
                    // Vehicle out transaction log exists
                    // Process the vehicle out data
                    response.setVehicleOut(transaction.getVehicleOut());
                }
                response.setPoNo(transaction.getPoNo());
                response.setChallanNo(transaction.getChallanNo());
                response.setTpNo(transaction.getTpNo());
                response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
                response.setTransporter(transporterName);
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



}
 