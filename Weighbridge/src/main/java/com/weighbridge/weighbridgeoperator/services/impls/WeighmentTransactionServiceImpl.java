package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.SalesManagement.entities.SalesOrder;
import com.weighbridge.SalesManagement.entities.SalesProcess;
import com.weighbridge.SalesManagement.repositories.SalesOrderRespository;
import com.weighbridge.SalesManagement.repositories.SalesProcessRepository;
import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.weighbridgeoperator.entities.VehicleTransactionStatus;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;


import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgePageResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentRequest;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;


    @Override
    public String saveWeight(WeighmentRequest weighmentRequest,String userId) {
        // Set user session details
    /*    HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }*/
        GateEntryTransaction gateEntryId = gateEntryTransactionRepository.findById(weighmentRequest.getTicketNo()).get();
        WeighmentTransaction weighmentTicketNo = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(weighmentRequest.getTicketNo());
        VehicleTransactionStatus byTicketNo = vehicleTransactionStatusRepository.findByTicketNo(weighmentRequest.getTicketNo());
        if (weighmentTicketNo == null) {
            WeighmentTransaction weighmentTransaction = new WeighmentTransaction();
            weighmentTransaction.setGateEntryTransaction(gateEntryId);
            weighmentTransaction.setMachineId(weighmentRequest.getMachineId());
            weighmentTransaction.setTemporaryWeight(weighmentRequest.getWeight()/1000);
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
            double secondWeight=weighmentRequest.getWeight()/1000;
            if (temporaryWeight > secondWeight) {
                weighmentTicketNo.setGrossWeight(temporaryWeight);
                weighmentTicketNo.setTareWeight(secondWeight);
            } else {
                weighmentTicketNo.setTareWeight(temporaryWeight);
                weighmentTicketNo.setGrossWeight(secondWeight);
            }
            double netWeight = Math.abs(temporaryWeight - secondWeight)/1000;
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
                double progressiveQty = bySaleOrderNo.getProgressiveQuantity() + netWeight;
                double balanceQty = bySaleOrderNo.getBalanceQuantity() - progressiveQty ;
                bySaleOrderNo.setProgressiveQuantity(progressiveQty);
                bySaleOrderNo.setBalanceQuantity(balanceQty);
                salesOrderRespository.save(bySaleOrderNo);
            }
            return "Second weight saved";
        }
    }

    @Override
    public WeighbridgePageResponse getAllGateDetails(Pageable pageable,String userId) {
       /* HttpSession session = httpServletRequest.getSession();
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
        System.out.println(userSite);*/
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("userId not found"));
        Page<Object[]> pageResult = weighmentTransactionRepository.getAllGateEntries(byId.getSite().getSiteId(),byId.getCompany().getCompanyId(),pageable);
        List<Object[]> allUsers = pageResult.getContent();
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
                    LocalDateTime timestamp = null, timestamp1 = null;
                    String restTimeStamp=null,restTimeStamp1=null;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    if (byTicketNo != null) {
                        timestamp = byTicketNo.getTimestamp();
                        restTimeStamp = timestamp != null ? timestamp.format(formatter) : "";
                    }
                    if (byTicketNo2 != null) {
                        timestamp1 = byTicketNo2.getTimestamp();
                        restTimeStamp1 = timestamp1 != null ? timestamp1.format(formatter) : "";
                    }
                    String weighmentNo = row[1]!=null ? String.valueOf(row[1]):" ";
                    response.setWeighmentNo(weighmentNo);
                    response.setTransactionType((String) row[2]);
                    response.setTransactionDate((LocalDate)row[3]);
                    LocalDateTime vehicleInDateTime = (LocalDateTime) row[4];
                    response.setVehicleIn(vehicleInDateTime.format(formatter));
                    if (((String) row[2]).equalsIgnoreCase("Inbound")) {
                        if(row[8]!=null&&row[6]!=null) {
                            response.setGrossWeight(row[8] + "/" + restTimeStamp);
                            response.setTareWeight(row[6] + "/" + restTimeStamp1);
                        }
                        else{
                            response.setGrossWeight("");
                            response.setTareWeight("");
                        }
                    } else {
                        if(row[8]!=null&&row[5]!=null) {
                            response.setTareWeight(String.valueOf(row[8]) + "/" + restTimeStamp1);
                            response.setGrossWeight(row[5] + "/" + restTimeStamp);
                        }
                        else{
                            response.setGrossWeight("");
                            response.setTareWeight("");
                        }
                    }
                    response.setNetWeight(row[7] != null ? String.valueOf(row[7]) : "");
                    response.setVehicleNo((String) row[9]);
                    response.setVehicleFitnessUpTo((LocalDate) row[10]);
                    if (((String) row[2]).equalsIgnoreCase("Inbound")) {
                        response.setSupplierName((String) row[11]);
                        response.setCustomerName("");
                        response.setMaterialName((String) row[13]);
                    } else {
                        response.setCustomerName((String) row[11]);
                        response.setSupplierName("");
                        response.setMaterialName((String) row[13]);
                    }

                    response.setTransporterName((String) row[12]);
                    // Set other fields similarly
                    responses.add(response);
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            System.out.println(responses);
            WeighbridgePageResponse weighbridgePageResponse=new WeighbridgePageResponse();
            weighbridgePageResponse.setWeighmentTransactionResponses(responses);
            weighbridgePageResponse.setTotalPages((long) pageResult.getTotalPages());
            weighbridgePageResponse.setTotalElements(pageResult.getTotalElements());
            return weighbridgePageResponse;
        }
    }

    @Override
    public TicketResponse getResponseByTicket(Integer ticketNo) {
        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findById(ticketNo).get();
        if (gateEntryTransaction == null) {
            throw new ResourceNotFoundException("ticket", "ticketNo", ticketNo.toString());
        }
        else {
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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            if (grossWeightTime != null) {
                ticketResponse.setGrossWeightTime(grossWeightTime.format(formatter));
            }

            if (tareWeightTime != null) {
                ticketResponse.setTareWeightTime(tareWeightTime.format(formatter));
            }

            String transactionType = gateEntryTransaction.getTransactionType();
            WeighmentTransaction byGateEntryTransactionTicketNo = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);

                if (transactionType.equalsIgnoreCase("Inbound")) {
                    if (byGateEntryTransactionTicketNo != null) {
                        ticketResponse.setGrossWeight(byGateEntryTransactionTicketNo.getTemporaryWeight());
                        ticketResponse.setTareWeight(byGateEntryTransactionTicketNo.getTareWeight());
                    }
                    Object[] supplierInfo = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(gateEntryTransaction.getSupplierId());
                    Object[] supplierData = (Object[]) supplierInfo[0];
                    if (supplierData != null && supplierData.length >= 2) {
                        String supplierName = (String) supplierData[0];
                        String supplierAddress = (String) supplierData[1];
                        System.out.println(supplierName + " " + supplierAddress);
                        ticketResponse.setSupplierName(supplierName);
                        ticketResponse.setSupplierAddress(supplierAddress);
                    }
                }
                if (transactionType.equalsIgnoreCase("Outbound")) {
                    if (byGateEntryTransactionTicketNo != null) {
                        ticketResponse.setTareWeight(byGateEntryTransactionTicketNo.getTemporaryWeight());
                        ticketResponse.setGrossWeight(byGateEntryTransactionTicketNo.getGrossWeight());
                    }
                    Object[] customerInfo = customerMasterRepository.findCustomerNameAndAddressBycustomerId(gateEntryTransaction.getCustomerId());
                    Object[] customerData = (Object[]) customerInfo[0];
                    if (customerData != null && customerData.length >= 2) {
                        String customerName = (String) customerData[0];
                        String customerAddress = (String) customerData[1];
                        System.out.println(customerName + " " + customerAddress);
                        ticketResponse.setCustomerName(customerName);
                        ticketResponse.setCustomerAdress(customerAddress);
                    }
                }
            if (byGateEntryTransactionTicketNo != null) {
                ticketResponse.setNetWeight(byGateEntryTransactionTicketNo.getGrossWeight() - byGateEntryTransactionTicketNo.getTareWeight());
            }
            ticketResponse.setDriverDlNo(gateEntryTransaction.getDlNo());
            Double supplyConsignmentWeight = gateEntryTransaction.getSupplyConsignmentWeight();
            if(supplyConsignmentWeight!=null) {
                ticketResponse.setConsignmentWeight(supplyConsignmentWeight*1000);
            }
            else{
                ticketResponse.setConsignmentWeight(0.0);
            }
            return ticketResponse;
        }
    }

    /**
     * @param pageable
     * @return
     */
    @Override
    public WeighbridgePageResponse getAllCompletedTickets(Pageable pageable,String userId) {
       /* HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;

        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }*/
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found with"+userId));
        Page<WeighmentTransaction> all = weighmentTransactionRepository.findAllByUserSiteAndUserCompany(byId.getSite().getSiteId(),byId.getCompany().getCompanyId(),pageable);
        List<WeighmentTransaction> allUsers = all.getContent();
        if(allUsers==null){
            throw new ResourceNotFoundException("No response found.");
        }
        List<WeighmentTransactionResponse> weighmentTransactionResponses=new ArrayList<>();
        for(WeighmentTransaction weighmentTransaction:allUsers){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                WeighmentTransactionResponse weighmentTransactionResponse = new WeighmentTransactionResponse();
                weighmentTransactionResponse.setTransactionDate(weighmentTransaction.getGateEntryTransaction().getTransactionDate());
                weighmentTransactionResponse.setTransactionType(weighmentTransaction.getGateEntryTransaction().getTransactionType());
                weighmentTransactionResponse.setWeighmentNo(String.valueOf(weighmentTransaction.getWeighmentNo()));
                weighmentTransactionResponse.setTicketNo(String.valueOf(weighmentTransaction.getGateEntryTransaction().getTicketNo()));
                weighmentTransactionResponse.setVehicleIn(weighmentTransaction.getGateEntryTransaction().getVehicleIn().format(formatter));
                weighmentTransactionResponse.setNetWeight(String.valueOf(weighmentTransaction.getNetWeight()));
                weighmentTransactionResponse.setGrossWeight(String.valueOf(weighmentTransaction.getGrossWeight()));
                weighmentTransactionResponse.setTareWeight(String.valueOf(weighmentTransaction.getTareWeight()));
                weighmentTransactionResponse.setVehicleNo(vehicleMasterRepository.findVehicleNoById(weighmentTransaction.getGateEntryTransaction().getVehicleId()));
                weighmentTransactionResponse.setVehicleFitnessUpTo(vehicleMasterRepository.findVehicleFitnessById(weighmentTransaction.getGateEntryTransaction().getVehicleId()));
                if(weighmentTransaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")){
                    weighmentTransactionResponse.setMaterialName(materialMasterRepository.findMaterialNameByMaterialId(weighmentTransaction.getGateEntryTransaction().getMaterialId()));
                    weighmentTransactionResponse.setSupplierName(supplierMasterRepository.findSupplierNameBySupplierId(weighmentTransaction.getGateEntryTransaction().getSupplierId()));
                }
                else{
                    weighmentTransactionResponse.setMaterialName(productMasterRepository.findProductNameByProductId(weighmentTransaction.getGateEntryTransaction().getMaterialId()));
                    weighmentTransactionResponse.setCustomerName(customerMasterRepository.findCustomerNameByCustomerId(weighmentTransaction.getGateEntryTransaction().getCustomerId()));
                }

                weighmentTransactionResponse.setTransporterName(transporterMasterRepository.findTransporterNameByTransporterId(weighmentTransaction.getGateEntryTransaction().getTransporterId()));
                weighmentTransactionResponses.add(weighmentTransactionResponse);


        }
        long count = weighmentTransactionRepository.countCompletedTransactions();
        WeighbridgePageResponse weighbridgePageResponse=new WeighbridgePageResponse();
        weighbridgePageResponse.setWeighmentTransactionResponses(weighmentTransactionResponses);
        weighbridgePageResponse.setTotalPages(count/ all.getSize());
        weighbridgePageResponse.setTotalElements(count);
        return weighbridgePageResponse;
    }
}