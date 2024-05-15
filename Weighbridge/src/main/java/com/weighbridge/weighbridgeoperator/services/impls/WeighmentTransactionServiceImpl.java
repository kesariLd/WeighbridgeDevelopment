package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.SalesManagement.entities.SalesOrder;
import com.weighbridge.SalesManagement.entities.SalesProcess;
import com.weighbridge.SalesManagement.repositories.SalesOrderRespository;
import com.weighbridge.SalesManagement.repositories.SalesProcessRepository;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;


import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WeighmentTransactionServiceImpl implements WeighmentTransactionService {
    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private SalesProcessRepository salesProcessRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;
    @Autowired
    private SalesOrderRespository salesOrderRespository;


    @Override
    public String saveWeight(WeighmentRequest weighmentRequest) {
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
        GateEntryTransaction gateEntryId = gateEntryTransactionRepository.findById(weighmentRequest.getTicketNo()).get();
        WeighmentTransaction weighmentTicketNo = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(weighmentRequest.getTicketNo());
        VehicleTransactionStatus byTicketNo = vehicleTransactionStatusRepository.findByTicketNo(weighmentRequest.getTicketNo());
        if (weighmentTicketNo == null) {
            WeighmentTransaction weighmentTransaction = new WeighmentTransaction();
            weighmentTransaction.setGateEntryTransaction(gateEntryId);
            weighmentTransaction.setMachineId(weighmentRequest.getMachineId());
            weighmentTransaction.setTemporaryWeight(weighmentRequest.getWeight());
            weighmentTransactionRepository.save(weighmentTransaction);

            //History save with vehicle intime and vehicle out time
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(weighmentRequest.getTicketNo());
            transactionLog.setTimestamp(LocalDateTime.now());

            if (gateEntryId.getTransactionType().equalsIgnoreCase("Inbound")) {
                byTicketNo.setStatusCode("GWT");
                transactionLog.setStatusCode("GWT");
            } else {
                byTicketNo.setStatusCode("TWT");
                transactionLog.setStatusCode("TWT");
            }
            vehicleTransactionStatusRepository.save(byTicketNo);
            transactionLogRepository.save(transactionLog);
            return "First Weight saved.";
        } else {
            //History save with vehicle intime and vehicle out time
            if (gateEntryId.getTransactionType().equalsIgnoreCase("Inbound") && byTicketNo.getStatusCode().equalsIgnoreCase("TWT")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tare Weight already saved.");
            }

            if (gateEntryId.getTransactionType().equalsIgnoreCase("Outbound") && byTicketNo.getStatusCode().equalsIgnoreCase("GWT")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gross Weight already saved.");
            }

            double temporaryWeight = weighmentTicketNo.getTemporaryWeight();
            if (temporaryWeight > weighmentRequest.getWeight()) {
                weighmentTicketNo.setGrossWeight(temporaryWeight);
                weighmentTicketNo.setTareWeight(weighmentRequest.getWeight());
            } else {
                weighmentTicketNo.setTareWeight(temporaryWeight);
                weighmentTicketNo.setGrossWeight(weighmentRequest.getWeight());
            }
            double netWeight = Math.abs(temporaryWeight - weighmentRequest.getWeight());
            weighmentTicketNo.setNetWeight(netWeight);

            weighmentTransactionRepository.save(weighmentTicketNo);
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setUserId(userId);
            transactionLog.setTicketNo(weighmentRequest.getTicketNo());
            transactionLog.setTimestamp(LocalDateTime.now());


            //Vehiclestatus details
            if (gateEntryId.getTransactionType().equalsIgnoreCase("Outbound")) {
                byTicketNo.setStatusCode("GWT");
                transactionLog.setStatusCode("GWT");
            } else {
                byTicketNo.setStatusCode("TWT");
                transactionLog.setStatusCode("TWT");
            }
            vehicleTransactionStatusRepository.save(byTicketNo);
            transactionLogRepository.save(transactionLog);

            if (gateEntryId.getTransactionType().equalsIgnoreCase("Outbound")) {
                SalesProcess bySalePassNo = salesProcessRepository.findBySalePassNo(gateEntryId.getTpNo());
                SalesOrder bySaleOrderNo = salesOrderRespository.findBySaleOrderNo(bySalePassNo.getPurchaseSale().getSaleOrderNo());
                double progressiveQty = bySaleOrderNo.getProgressiveQuantity() + netWeight / 1000;
                double balanceQty = bySaleOrderNo.getBalanceQuantity() - progressiveQty / 1000;
                bySaleOrderNo.setProgressiveQuantity(progressiveQty);
                bySaleOrderNo.setBalanceQuantity(balanceQty);
                salesOrderRespository.save(bySaleOrderNo);
            }
            return "Second weight saved";
        }
    }

    @Override
    public List<WeighmentTransactionResponse> getAllGateDetails() {
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
        System.out.println(userSite);
        List<Object[]> allUsers = weighmentTransactionRepository.getAllGateEntries(userSite);
        System.out.println(allUsers);
        List<WeighmentTransactionResponse> responses = new ArrayList<>();
        if (allUsers == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No gateEntries yet.");
        } else {
            try {
                for (Object[] row : allUsers) {
                    WeighmentTransactionResponse response = new WeighmentTransactionResponse();
                    response.setTicketNo(String.valueOf(row[0]));
                    TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode((Integer) row[0], "GWT");
                    TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode((Integer) row[0], "TWT");
                    LocalDateTime timestamp = null, timestamp1 = null, resTimeStamp = null, resTimeStamp1 = null;
                    if (byTicketNo != null) {
                        timestamp = byTicketNo.getTimestamp();
                        resTimeStamp = timestamp.withSecond(0).withNano(0);
                    }
                    if (byTicketNo2 != null) {
                        timestamp1 = byTicketNo2.getTimestamp();
                        resTimeStamp1 = timestamp1.withSecond(0).withNano(0);
                    }
                    String weighmentNo = row[1]!=null ? String.valueOf(row[1]):" ";
                    response.setWeighmentNo(weighmentNo);
                    response.setTransactionType((String) row[2]);
                    response.setTransactionDate((LocalDate) row[3]);
                    response.setVehicleIn((LocalDateTime) row[4]);
                    if (((String) row[2]).equalsIgnoreCase("Inbound")) {
                        if(row[8]!=null&&row[6]!=null) {
                            response.setGrossWeight(String.valueOf(row[8]) + "/" + resTimeStamp);
                            response.setTareWeight(row[6] + "/" + resTimeStamp1);
                        }
                        else{
                            response.setGrossWeight("");
                            response.setTareWeight("");
                        }
                    } else {
                        if(row[8]!=null&&row[5]!=null) {
                            response.setTareWeight(String.valueOf(row[8]) + "/" + resTimeStamp1);
                            response.setGrossWeight(row[5] + "/" + resTimeStamp);
                        }
                        else{
                            response.setGrossWeight("");
                            response.setTareWeight("");
                        }
                    }
                    response.setNetWeight(String.valueOf(row[7] + "/" + resTimeStamp1));
                    response.setVehicleNo((String) row[9]);
                    response.setVehicleFitnessUpTo((LocalDate) row[10]);
                    if (((String) row[2]).equalsIgnoreCase("Inbound")) {
                        response.setSupplierName((String) row[11]);
                        response.setCustomerName("");
                    } else {
                        response.setCustomerName((String) row[11]);
                        response.setSupplierName("");
                    }

                    response.setTransporterName((String) row[12]);
                    response.setMaterialName((String) row[13]);
                    // Set other fields similarly
                    responses.add(response);
                }

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            System.out.println(responses);
            return responses;
        }
    }

    // todo NWT status insert
    @Override
    public TicketResponse getResponseByTicket(Integer ticketNo) {
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findById(ticketNo).get();
        if (gateEntryTransaction == null) {
            throw new ResourceNotFoundException("ticket", "ticketNo", ticketNo.toString());
        } else {
            System.out.println("site id" + gateEntryTransaction.getSupplierId());
            TicketResponse ticketResponse = new TicketResponse();
            ticketResponse.setPoNo(gateEntryTransaction.getPoNo());
            ticketResponse.setTpNo(gateEntryTransaction.getTpNo());
            ticketResponse.setChallanNo(gateEntryTransaction.getChallanNo());
            ticketResponse.setMaterial(materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId()));
            ticketResponse.setTransporter(transporterMasterRepository.findTransporterNameByTransporterId(gateEntryTransaction.getTransporterId()));
            ticketResponse.setDriverName(gateEntryTransaction.getDriverName());
            VehicleMaster vehicleMaster = vehicleMasterRepository.findById(gateEntryTransaction.getVehicleId()).orElseThrow(() -> new ResourceNotFoundException("Vehicle is not found"));

            ticketResponse.setVehicleNo(vehicleMaster.getVehicleNo());

            Optional<TransactionLog> byTicketNoGWT = Optional.ofNullable(transactionLogRepository.findByTicketNoAndStatusCode(ticketNo, "GWT"));
            Optional<TransactionLog> byTicketNoTWT = Optional.ofNullable(transactionLogRepository.findByTicketNoAndStatusCode(ticketNo, "TWT"));

            LocalDateTime grossWeightTime = byTicketNoGWT.map(TransactionLog::getTimestamp).map(t -> t.withSecond(0).withNano(0)).orElse(null);
            LocalDateTime tareWeightTime = byTicketNoTWT.map(TransactionLog::getTimestamp).map(t -> t.withSecond(0).withNano(0)).orElse(null);

            ticketResponse.setGrossWeightTime(grossWeightTime);
            ticketResponse.setTareWeightTime(tareWeightTime);

            String transactionType = gateEntryTransaction.getTransactionType();
            WeighmentTransaction byGateEntryTransactionTicketNo = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);
            if (byGateEntryTransactionTicketNo != null) {
                if (transactionType.equalsIgnoreCase("Inbound")) {
                    ticketResponse.setGrossWeight(byGateEntryTransactionTicketNo.getTemporaryWeight());
                    ticketResponse.setTareWeight(byGateEntryTransactionTicketNo.getTareWeight());
                    Object[] supplierInfo = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(gateEntryTransaction.getSupplierId());
                    Object[] supplierData = (Object[]) supplierInfo[0];
                    if (supplierData != null && supplierData.length >= 2) {
                        String supplierName = (String) supplierData[0];
                        String supplierAddress = (String) supplierData[1];
                        System.out.println(supplierName + " " + supplierAddress);
                        ticketResponse.setSupplierName(supplierName);
                        ticketResponse.setSupplierAddress(supplierAddress);
                    }
                } else {
                    ticketResponse.setTareWeight(byGateEntryTransactionTicketNo.getTemporaryWeight());
                    ticketResponse.setGrossWeight(byGateEntryTransactionTicketNo.getGrossWeight());
                    Object[] customerInfo = customerMasterRepository.findCustomerNameAndAddressBycustomerId(gateEntryTransaction.getCustomerId());
                    Object[] customerData = (Object[]) customerInfo[0];
                    if (customerData != null && customerData.length >= 2) {
                        String customerName = (String) customerData[0];
                        String customerAddress = (String) customerData[1];
                        System.out.println(customerName + " " + customerAddress);
                        ticketResponse.setSupplierName(customerName);
                        ticketResponse.setSupplierAddress(customerAddress);
                    }
                }
                ticketResponse.setNetWeight(byGateEntryTransactionTicketNo.getGrossWeight() - byGateEntryTransactionTicketNo.getTareWeight());
            }
//            ticketResponse.setSupplierName(supplierName);
            ticketResponse.setDriverDlNo(gateEntryTransaction.getDlNo());
//            ticketResponse.setSupplierAddress(supplierAddress);
            return ticketResponse;
        }
    }
}