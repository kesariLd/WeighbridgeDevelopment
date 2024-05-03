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
        // Set user session details
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }

        //getting the name and details of request data
        String materialName = gateEntryTransactionRequest.getMaterial();
        String vehicleNo = gateEntryTransactionRequest.getVehicle();
        String transporterName = gateEntryTransactionRequest.getTransporter();
        String supplierName = gateEntryTransactionRequest.getSupplier();
        String supplierAddress = gateEntryTransactionRequest.getSupplierAddressLine1();
        String supplierAddressLine1;
        String supplierAddressLine2;
        if (supplierAddress != null && supplierAddress.contains(",")) {
            String[] parts = supplierAddress.split(",", 2); // Split into two parts
            supplierAddressLine1 = parts[0].trim(); // Trim to remove leading/trailing spaces
            supplierAddressLine2 = parts[1].trim();
        } else {
            // If there's no comma, store everything in supplierAddressLine1
            supplierAddressLine1 = supplierAddress;
            supplierAddressLine2 = null; // Set supplierAddressLine2 to null
        }
        //finding the entities by names from database
        long materialId = materialMasterRepository.findByMaterialIdByMaterialName(materialName);
        long vehicleId = vehicleMasterRepository.findVehicleIdByVehicleNo(vehicleNo);
        long transporterId = transporterMasterRepository.findTransporterIdByTransporterName(transporterName);
        Long supplierId = supplierMasterRepository.findSupplierIdBySupplierNameAndAddressLines(
                supplierName, supplierAddressLine1, supplierAddressLine2);
        if (supplierId == null) {
            throw new ResourceNotFoundException("Supplier not exist");
        }
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
        VehicleTransactionStatus vehicleTransactionStatus = vehicleTransactionStatusRepository.findByTicketNo(ticketNo);
        HttpSession session = httpServletRequest.getSession();
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(ticketNo);
        if (!Objects.equals(vehicleTransactionStatus.getStatusCode(), "TWT")) {
            return "vehicle TareWeight is not measured yet!";
        }
        String userId;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setUserId(userId);
        transactionLog.setTimestamp(LocalDateTime.now());
        transactionLog.setTicketNo(ticketNo);
        transactionLog.setStatusCode("GXT");
        transactionLogRepository.save(transactionLog);
        vehicleTransactionStatus.setStatusCode("GXT");
        LocalDateTime now = LocalDateTime.now();
        // Round up the seconds
        LocalDateTime vehicleOutTime = now.withSecond(0).withNano(0);
        gateEntryTransaction.setVehicleOut(vehicleOutTime);
        vehicleTransactionStatusRepository.save(vehicleTransactionStatus);
        return "Vehicle Can exit";
    }
    @Override
    public List<GateEntryTransactionResponse> getAllGateEntryTransaction() {
        // Set user session details
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }
        List<GateEntryTransaction> allTransactions = gateEntryTransactionRepository.findBySiteIdAndCompanyIdOrderByTicketNoDesc(userSite,userCompany);
        System.out.println("GateEntryTransactionServiceImpl.getAllGateEntryTransaction"+allTransactions);
        List<GateEntryTransactionResponse> responseList = new ArrayList<>();
        for (GateEntryTransaction transaction : allTransactions) {
            GateEntryTransactionResponse response = new GateEntryTransactionResponse();
            // Fetching associated entity names
            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
            System.out.println("vehicle id"+transaction.getVehicleId());
            System.out.println(" hasg"+vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId()));
            Object[] vehicleNoAndVehicleTypeAndAndvehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());
            System.out.println("vehicle "+vehicleNoAndVehicleTypeAndAndvehicleWheelsNoByVehicleId[0]);
            String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
            Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameBySupplierId(transaction.getSupplierId());
            // Setting values to response object
            response.setTicketNo(transaction.getTicketNo());
            response.setTransactionType(transaction.getTransactionType());
            response.setMaterial(materialName);
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
            TransactionLog transactionLogVehicleIn = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(),"GNT");
            TransactionLog transactionLogVehicleOut = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(),"GXT");
            // Check if vehicle out transaction log exists
            if (transactionLogVehicleIn != null) {
                // Vehicle out transaction log exists
                // Process the vehicle out data
                LocalDateTime vehicleInTime = transactionLogVehicleIn.getTimestamp();
                response.setVehicleIn(vehicleInTime);
            }
            // Check if vehicle out transaction log exists
            if (transactionLogVehicleOut != null) {
                // Vehicle out transaction log exists
                // Process the vehicle out data
                LocalDateTime vehicleOutTime = transactionLogVehicleOut.getTimestamp();
                response.setVehicleOut(vehicleOutTime);
            }
            response.setPoNo(transaction.getPoNo());
            response.setChallanNo(transaction.getChallanNo());
            response.setTpNo(transaction.getTpNo());
            response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
            response.setTransporter(transporterName);
            responseList.add(response);
        }
        return responseList;
    }
}
 