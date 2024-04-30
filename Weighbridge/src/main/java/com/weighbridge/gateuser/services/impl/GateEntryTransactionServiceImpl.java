package com.weighbridge.gateuser.services.impl;

import com.weighbridge.admin.entities.*;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
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

/**
 * GateEntryTransactionServiceImpl class manage all gate entry transaction
 */
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
                throw new SessionExpiredException("Session Expired, Login again !");
            }

            // Extracting and processing supplier address
            String materialName = gateEntryTransactionRequest.getMaterial();
            String vehicleNo = gateEntryTransactionRequest.getVehicle();
            String transporterName = gateEntryTransactionRequest.getTransporter();
            String supplierName = gateEntryTransactionRequest.getSupplier();
            String supplierAddress = gateEntryTransactionRequest.getSupplierAddressLine1();
            String supplierAddressLine1;
            String supplierAddressLine2;
            //Extract the supplier address so that , can search on database it's exist or not
            if (supplierAddress != null && supplierAddress.contains(",")) {
                String[] parts = supplierAddress.split(",", 2); // Split into two parts
                supplierAddressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
                supplierAddressLine2 = parts[1].trim();
            } else {
                // If there's no comma, store everything in supplierAddressLine1
                supplierAddressLine1 = supplierAddress;
                supplierAddressLine2 = null; // Set supplierAddressLine2 to null
            }

            // Finding entity IDs by names from the database
            long materialId = materialMasterRepository.findByMaterialIdByMaterialName(materialName);
            long vehicleId = vehicleMasterRepository.findVehicleIdByVehicleNo(vehicleNo);
            long transporterId = transporterMasterRepository.findTransporterIdByTransporterName(transporterName);
            //here finding the supplierId , it's existing or not in database
            Long supplierId = supplierMasterRepository.findSupplierIdBySupplierNameAndAddressLines(
                    supplierName, supplierAddressLine1, supplierAddressLine2);
            if (supplierId == null) {
                throw new ResourceNotFoundException("Supplier not exist");
            }

            // Assigning values to the gate entry transaction entity
            GateEntryTransaction gateEntryTransaction = new GateEntryTransaction();
            gateEntryTransaction.setTransactionDate(LocalDate.now());
            gateEntryTransaction.setCompanyId(userCompany);
            gateEntryTransaction.setTransporterId(transporterId);
            gateEntryTransaction.setVehicleId(vehicleId);
            gateEntryTransaction.setMaterialId(materialId);
            gateEntryTransaction.setSupplierId(supplierId);
            gateEntryTransaction.setSiteId(userSite);
            gateEntryTransaction.setDlNo(gateEntryTransactionRequest.getDlNo());
            gateEntryTransaction.setDriverName(gateEntryTransactionRequest.getDriverName());
            gateEntryTransaction.setSupplyConsignmentWeight(gateEntryTransactionRequest.getSupplyConsignmentWeight());
            gateEntryTransaction.setPoNo(gateEntryTransactionRequest.getPoNo());
            gateEntryTransaction.setTpNo(gateEntryTransactionRequest.getTpNo());
            gateEntryTransaction.setChallanNo(gateEntryTransactionRequest.getChallanNo());
            gateEntryTransaction.setEwaybillNo(gateEntryTransactionRequest.getEwayBillNo());
            gateEntryTransaction.setTransactionType("Inbound");

            // Save gate entry transaction
            GateEntryTransaction savedGateEntryTransaction = gateEntryTransactionRepository.save(gateEntryTransaction);

            // Save vehicle transaction status
            VehicleTransactionStatus vehicleTransactionStatus = new VehicleTransactionStatus();
            Integer ticketNo = savedGateEntryTransaction.getTicketNo();
            vehicleTransactionStatus.setTicketNo(ticketNo);
            vehicleTransactionStatus.setStatusCode("GNT");
            vehicleTransactionStatusRepository.save(vehicleTransactionStatus);

            // Save transaction log
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(ticketNo);
            transactionLog.setTimestamp(LocalDateTime.now());
            transactionLog.setStatusCode("GNT");
            transactionLogRepository.save(transactionLog);

            return ticketNo;
        }
        catch (SessionExpiredException se){
            throw se;
        }
        catch (Exception e) {
            // Log the exception
            // Rethrow the exception as ResponseStatusException
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while saving gate entry transaction", e);
        }
    }


    /*@Override
    public List<GateEntryTransaction> getAllGateEntryTraansaction() {
        return gateEntryTransactionRepository.findAll();
    }*/

    /**
     * Sets the out time for a vehicle based on its ticket number.
     *
     * @param ticketNo The ticket number of the vehicle.
     *                 if the ticket number status is TWT or tare weight than only vehicle can exit
     * @return A message indicating whether the vehicle can exit or not.
     * @throws ResponseStatusException If the session is expired and login is required, or if the vehicle's tare weight is not measured yet.
     */
    @Override
    public String setOutTime(Integer ticketNo) {
        try {
            // Retrieve vehicle transaction status
            VehicleTransactionStatus vehicleTransactionStatus = vehicleTransactionStatusRepository.findByTicketNo(ticketNo);

            // Check if vehicle tare weight is measured
            if (!Objects.equals(vehicleTransactionStatus.getStatusCode(), "TWT")) {
                return "Vehicle tare weight is not measured yet!";
            }

            // Retrieve user session details
            HttpSession session = httpServletRequest.getSession();
            String userId;
            if (session != null && session.getAttribute("userId") != null) {
                userId = session.getAttribute("userId").toString();
            } else {
                throw new SessionExpiredException("Session Expired, Login again !");
            }

            // Save transaction log
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTimestamp(LocalDateTime.now());
            transactionLog.setTicketNo(ticketNo);
            transactionLog.setStatusCode("GXT");
            transactionLogRepository.save(transactionLog);

            // Update vehicle transaction status
            vehicleTransactionStatus.setStatusCode("GXT");
            vehicleTransactionStatusRepository.save(vehicleTransactionStatus);

            return "Vehicle can exit";
        }
        catch (SessionExpiredException se){
            throw se;
        }catch (Exception e) {
            // Log the exception
            // Rethrow the exception as ResponseStatusException
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while setting out time for vehicle", e);
        }
    }


    /**
     * Retrieves all gate entry transactions associated with the current user session.
     *
     * @return A list of gate entry transaction responses, containing the following details:
     * - Ticket number (ticketNo)
     * - Transaction type (transactionType)
     * - Material name (material)
     * - Supplier name (supplier)
     * - Supplier address (supplierAddress)
     * - Vehicle number (vehicleNo)
     * - Vehicle type (vehicleType)
     * - Number of vehicle wheels (vehicleWheelsNo)
     * - Vehicle in timestamp (vehicleIn)
     * - Vehicle out timestamp (vehicleOut)
     * - Purchase order number (poNo)
     * - Challan number (challanNo)
     * - Transport permit number (tpNo)
     * - Tare weight of the vehicle (tpNetWeight)
     * - Transporter name (transporter)
     *
     * @throws ResponseStatusException if the session is expired or if an internal server error occurs during the retrieval process.
     */
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

            // Retrieve all gate entry transactions based on user site and company
            List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyId(userSite, userCompany);
            List<GateEntryTransactionResponse> responseList = new ArrayList<>();
            for (GateEntryTransaction transaction : allTransactions) {
                GateEntryTransactionResponse response = new GateEntryTransactionResponse();

                // Fetching associated entity names
                String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
                Object[] vehicleNoAndVehicleTypeAndAndvehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());
                String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
                Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameBySupplierId(transaction.getSupplierId());

                // Setting values to response object
                response.setTicketNo(transaction.getTicketNo());
                response.setTransactionType(transaction.getTransactionType());
                response.setMaterial(materialName);

                // Setting supplier details
                Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
                if (supplierInfo != null && supplierInfo.length >= 2) {
                    String supplierName = (String) supplierInfo[0];
                    String supplierAddress = (String) supplierInfo[1];
                    response.setSupplier(supplierName);
                    response.setSupplierAddress(supplierAddress);
                } else {
                    // Handle case where supplier info is not available
                    response.setSupplier(null);
                    response.setSupplierAddress(null);
                }

                // Setting vehicle details
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

                // Setting vehicle in timestamp
                TransactionLog transactionLogVehicleIn = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "GNT");
                if (transactionLogVehicleIn != null) {
                    LocalDateTime vehicleInTime = transactionLogVehicleIn.getTimestamp();
                    response.setVehicleIn(vehicleInTime);
                }

                // Setting vehicle out timestamp
                TransactionLog transactionLogVehicleOut = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "GXT");
                if (transactionLogVehicleOut != null) {
                    LocalDateTime vehicleOutTime = transactionLogVehicleOut.getTimestamp();
                    response.setVehicleOut(vehicleOutTime);
                }

                // Setting other details
                response.setPoNo(transaction.getPoNo());
                response.setChallanNo(transaction.getChallanNo());
                response.setTpNo(transaction.getTpNo());
                response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
                response.setTransporter(transporterName);

                // Add response to list
                responseList.add(response);
            }

            return responseList;
        } catch (ResponseStatusException e) {
            // Rethrow the ResponseStatusException
            throw e;
        } catch (Exception e) {
            // Log the exception
            // Rethrow the exception as ResponseStatusException
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while fetching gate entry transactions", e);
        }
    }



}
