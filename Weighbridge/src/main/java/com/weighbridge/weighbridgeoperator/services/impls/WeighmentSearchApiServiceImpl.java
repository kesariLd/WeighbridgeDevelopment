package com.weighbridge.weighbridgeoperator.services.impls;


import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.weighbridgeoperator.dto.WeighbridgeOperatorSearchCriteria;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgePageResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentSearchApiService;
import com.weighbridge.weighbridgeoperator.specification.WeighmentTransactionSpecification;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WeighmentSearchApiServiceImpl implements WeighmentSearchApiService {

    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
     private HttpServletRequest httpServletRequest;

    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;

    /**
     * @return
     */
    @Override
    public WeighmentTransactionResponse getByTicketNo(Integer ticketNo) {
        GateEntryTransaction byWeighmentId =gateEntryTransactionRepository.findByTicketNo(ticketNo);
        if(byWeighmentId==null){
            throw new ResourceNotFoundException("ticket not found with ticketNo "+ticketNo);
        }
        WeighmentTransaction weight=weighmentTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);
        VehicleMaster byId = vehicleMasterRepository.findById(byWeighmentId.getVehicleId()).get();
        String customerNameByCustomerId = customerMasterRepository.findCustomerNameByCustomerId(byWeighmentId.getCustomerId());
        String supplierNameBySupplierIdsearchField = supplierMasterRepository.findSupplierNameBySupplierId(byWeighmentId.getSupplierId());
        String transporterNameByTransporterId = transporterMasterRepository.findTransporterNameByTransporterId(byWeighmentId.getTransporterId());
        if(weight!=null&&weight.getNetWeight()==0.0) {
            WeighmentTransactionResponse weighmentTransactionResponse = new WeighmentTransactionResponse();
            weighmentTransactionResponse.setTicketNo(String.valueOf(byWeighmentId.getTicketNo()));
            weighmentTransactionResponse.setWeighmentNo(weight != null ? String.valueOf(weight.getWeighmentNo()) : "");
            weighmentTransactionResponse.setVehicleFitnessUpTo(byId.getVehicleFitnessUpTo());
            weighmentTransactionResponse.setTransactionType(byWeighmentId.getTransactionType());
            weighmentTransactionResponse.setCustomerName(customerNameByCustomerId != null ? customerNameByCustomerId : "");
            weighmentTransactionResponse.setSupplierName(supplierNameBySupplierIdsearchField != null ? supplierNameBySupplierIdsearchField : "");
            weighmentTransactionResponse.setVehicleNo(byId.getVehicleNo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            weighmentTransactionResponse.setVehicleIn(byWeighmentId.getVehicleIn().format(formatter));
            weighmentTransactionResponse.setTransactionDate(byWeighmentId.getTransactionDate());
            TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode(byWeighmentId.getTicketNo(), "GWT");
            TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode(byWeighmentId.getTicketNo(), "TWT");
            LocalDateTime timestamp = null, timestamp1 = null;
            String restTimeStamp = null, restTimeStamp1 = null;
            if (byTicketNo != null) {
                timestamp = byTicketNo.getTimestamp();
                restTimeStamp = timestamp != null ? timestamp.format(formatter) : "";
            }
            if (byTicketNo2 != null) {
                timestamp1 = byTicketNo2.getTimestamp();
                restTimeStamp1 = timestamp1 != null ? timestamp1.format(formatter) : "";
            }
            if (byWeighmentId.getTransactionType().equalsIgnoreCase("Inbound")) {
                String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId(byWeighmentId.getMaterialId());
                weighmentTransactionResponse.setGrossWeight(String.valueOf(weight.getTemporaryWeight()) != null ? String.valueOf(weight.getTemporaryWeight()) + "/" + restTimeStamp : "");
                weighmentTransactionResponse.setTareWeight(String.valueOf(weight.getTareWeight()) != null ? String.valueOf(weight.getTareWeight()) + "/" + restTimeStamp1 : "");
                weighmentTransactionResponse.setMaterialName(materialNameByMaterialId != null ? materialNameByMaterialId : "");
            } else {
                String productNameByProductId = productMasterRepository.findProductNameByProductId(byWeighmentId.getMaterialId());
                weighmentTransactionResponse.setTareWeight(String.valueOf(weight.getTemporaryWeight()) != null ? String.valueOf(weight.getTemporaryWeight()) + "/" + restTimeStamp1 : "");
                weighmentTransactionResponse.setGrossWeight(String.valueOf(weight.getGrossWeight()) != null ? String.valueOf(weight.getGrossWeight()) + "/" + restTimeStamp : "");
                weighmentTransactionResponse.setMaterialName(productNameByProductId != null ? productNameByProductId : "");
            }
            weighmentTransactionResponse.setTransactionType(byWeighmentId.getTransactionType());
            weighmentTransactionResponse.setNetWeight(String.valueOf(weight.getNetWeight()) != null ? String.valueOf(weight.getNetWeight()) : "");
            weighmentTransactionResponse.setTransporterName(transporterNameByTransporterId != null ? transporterNameByTransporterId : "");
            return weighmentTransactionResponse;
        }
        else if(weight==null&&byWeighmentId!=null){
            WeighmentTransactionResponse weighmentTransactionResponse = new WeighmentTransactionResponse();
            weighmentTransactionResponse.setTicketNo(String.valueOf(byWeighmentId.getTicketNo()));
            weighmentTransactionResponse.setWeighmentNo(weight != null ? String.valueOf(weight.getWeighmentNo()) : "");
            weighmentTransactionResponse.setVehicleFitnessUpTo(byId.getVehicleFitnessUpTo());
            weighmentTransactionResponse.setTransactionType(byWeighmentId.getTransactionType());
            weighmentTransactionResponse.setCustomerName(customerNameByCustomerId != null ? customerNameByCustomerId : "");
            weighmentTransactionResponse.setSupplierName(supplierNameBySupplierIdsearchField != null ? supplierNameBySupplierIdsearchField : "");
            weighmentTransactionResponse.setVehicleNo(byId.getVehicleNo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            weighmentTransactionResponse.setVehicleIn(byWeighmentId.getVehicleIn().format(formatter));
            weighmentTransactionResponse.setTransactionDate(byWeighmentId.getTransactionDate());
            TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode(byWeighmentId.getTicketNo(), "GWT");
            TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode(byWeighmentId.getTicketNo(), "TWT");
            LocalDateTime timestamp = null, timestamp1 = null;
            String restTimeStamp = null, restTimeStamp1 = null;
            if (byTicketNo != null) {
                timestamp = byTicketNo.getTimestamp();
                restTimeStamp = timestamp != null ? timestamp.format(formatter) : "";
            }
            if (byTicketNo2 != null) {
                timestamp1 = byTicketNo2.getTimestamp();
                restTimeStamp1 = timestamp1 != null ? timestamp1.format(formatter) : "";
            }
            if (byWeighmentId.getTransactionType().equalsIgnoreCase("Inbound")) {
                String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId(byWeighmentId.getMaterialId());
                weighmentTransactionResponse.setGrossWeight("0.0");
                weighmentTransactionResponse.setTareWeight("0.0");
                weighmentTransactionResponse.setMaterialName(materialNameByMaterialId != null ? materialNameByMaterialId : "");
            } else {
                String productNameByProductId = productMasterRepository.findProductNameByProductId(byWeighmentId.getMaterialId());
                weighmentTransactionResponse.setTareWeight("0.0");
                weighmentTransactionResponse.setGrossWeight("0.0");
                weighmentTransactionResponse.setMaterialName(productNameByProductId != null ? productNameByProductId : "");
            }
            weighmentTransactionResponse.setTransactionType(byWeighmentId.getTransactionType());
            weighmentTransactionResponse.setNetWeight("0.0");
            weighmentTransactionResponse.setTransporterName(transporterNameByTransporterId != null ? transporterNameByTransporterId : "");
            return weighmentTransactionResponse;
        }
        else {
            throw new ResourceNotFoundException("ticket not found with ticketNo "+ticketNo);
        }
    }

    /**
     * @return
     */
    @Override
    public WeighbridgePageResponse getAllBySearchFields(WeighbridgeOperatorSearchCriteria criteria, Pageable pageable) {
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
        criteria.setSiteId(userSite);
        criteria.setCompanyId(userCompany);
        criteria.setUserId(userId);
        WeighmentTransactionSpecification specification = new WeighmentTransactionSpecification(criteria,vehicleMasterRepository,materialMasterRepository,transporterMasterRepository,productMasterRepository,supplierMasterRepository,customerMasterRepository);
        Page<WeighmentTransaction> pageResult = weighmentTransactionRepository.findAll(specification,pageable);
        List<WeighmentTransactionResponse> responses = pageResult.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        WeighbridgePageResponse response = new WeighbridgePageResponse();
        response.setWeighmentTransactionResponses(responses);
        response.setTotalPages(pageResult.getTotalPages());
        response.setTotalElements(pageResult.getTotalElements());
        return response;
    }

    private WeighmentTransactionResponse mapToResponse(WeighmentTransaction transaction){
        VehicleMaster byId = vehicleMasterRepository.findById(transaction.getGateEntryTransaction().getVehicleId()).get();
        String customerNameByCustomerId = customerMasterRepository.findCustomerNameByCustomerId(transaction.getGateEntryTransaction().getCustomerId());
        String supplierNameBySupplierIdsearchField = supplierMasterRepository.findSupplierNameBySupplierId(transaction.getGateEntryTransaction().getSupplierId());
        String transporterNameByTransporterId = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getGateEntryTransaction().getTransporterId());
        WeighmentTransactionResponse weighmentTransactionResponse=new WeighmentTransactionResponse();
        weighmentTransactionResponse.setTicketNo(String.valueOf(transaction.getGateEntryTransaction().getTicketNo()));
        weighmentTransactionResponse.setWeighmentNo(String.valueOf(transaction.getWeighmentNo()));
        weighmentTransactionResponse.setVehicleFitnessUpTo(byId.getVehicleFitnessUpTo());
        weighmentTransactionResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());
        weighmentTransactionResponse.setCustomerName(customerNameByCustomerId!=null?customerNameByCustomerId:"");
        weighmentTransactionResponse.setSupplierName(supplierNameBySupplierIdsearchField!=null?supplierNameBySupplierIdsearchField:"");
        weighmentTransactionResponse.setVehicleNo(byId.getVehicleNo());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        weighmentTransactionResponse.setVehicleIn(transaction.getGateEntryTransaction().getVehicleIn().format(formatter));
        weighmentTransactionResponse.setTransactionDate(transaction.getGateEntryTransaction().getTransactionDate());
        TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getGateEntryTransaction().getTicketNo(), "GWT");
        TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getGateEntryTransaction().getTicketNo(), "TWT");
        LocalDateTime timestamp = null, timestamp1 = null;
        String restTimeStamp=null,restTimeStamp1=null;
        if (byTicketNo != null) {
            timestamp = byTicketNo.getTimestamp();
            restTimeStamp = timestamp != null ? timestamp.format(formatter) : "";
        }
        if (byTicketNo2 != null) {
            timestamp1 = byTicketNo2.getTimestamp();
            restTimeStamp1 = timestamp1 != null ? timestamp1.format(formatter) : "";
        }
        if(transaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
            String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId(transaction.getGateEntryTransaction().getMaterialId());
            weighmentTransactionResponse.setGrossWeight(String.valueOf(transaction.getTemporaryWeight())!=null? String.valueOf(transaction.getTemporaryWeight())+"/"+restTimeStamp :"");
            weighmentTransactionResponse.setTareWeight(String.valueOf(transaction.getTareWeight())!=null? String.valueOf(transaction.getTareWeight())+"/"+restTimeStamp1 :"");
            weighmentTransactionResponse.setMaterialName(materialNameByMaterialId!=null?materialNameByMaterialId:"");
        }
        else {
            String productNameByProductId = productMasterRepository.findProductNameByProductId(transaction.getGateEntryTransaction().getMaterialId());
            weighmentTransactionResponse.setTareWeight(String.valueOf(transaction.getTemporaryWeight())!=null? String.valueOf(transaction.getTemporaryWeight())+"/"+restTimeStamp1 :"");
            weighmentTransactionResponse.setGrossWeight(String.valueOf(transaction.getGrossWeight())!=null? String.valueOf(transaction.getGrossWeight())+"/"+restTimeStamp :"");
            weighmentTransactionResponse.setMaterialName(productNameByProductId!=null?productNameByProductId:"");
        }
        weighmentTransactionResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());
        weighmentTransactionResponse.setNetWeight(String.valueOf(transaction.getNetWeight())!=null?String.valueOf(transaction.getNetWeight()):"");
        weighmentTransactionResponse.setTransporterName(transporterNameByTransporterId!=null?transporterNameByTransporterId:"");
        return weighmentTransactionResponse;
    }
}