package com.weighbridge.weighbridgeoperator.services.impls;


import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.UserMaster;
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
import com.weighbridge.weighbridgeoperator.specification.GateEntryTransactionSpecification;
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
import java.util.Objects;
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

    @Autowired
    private UserMasterRepository userMasterRepository;

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
    public WeighbridgePageResponse getAllBySearchFields(WeighbridgeOperatorSearchCriteria criteria, Pageable pageable,String userId) {
       UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found with"+userId));
        criteria.setSiteId(byId.getSite().getSiteId());
        criteria.setCompanyId(byId.getCompany().getCompanyId());
        criteria.setUserId(userId);
        WeighmentTransactionSpecification specification = new WeighmentTransactionSpecification(criteria,vehicleMasterRepository,materialMasterRepository,transporterMasterRepository,productMasterRepository,supplierMasterRepository,customerMasterRepository);
        Specification<WeighmentTransaction> netWeightNotNullSpec = WeighmentTransactionSpecification.netWeightNotZero();
        Specification<WeighmentTransaction> combinedSpec = Specification.where(specification).and(netWeightNotNullSpec);
        Page<WeighmentTransaction> pageResult = weighmentTransactionRepository.findAll(combinedSpec,pageable);
        List<WeighmentTransactionResponse> responses = pageResult.stream()
                .map(this::mapToResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            WeighbridgePageResponse response = new WeighbridgePageResponse();
            response.setWeighmentTransactionResponses(responses);
            response.setTotalPages((long) pageResult.getTotalPages());
            response.setTotalElements(pageResult.getTotalElements());
            return response;
    }

    private WeighmentTransactionResponse mapToResponse(WeighmentTransaction transaction){
        VehicleMaster byId = vehicleMasterRepository.findById(transaction.getGateEntryTransaction().getVehicleId()).get();
        String customerNameByCustomerId = customerMasterRepository.findCustomerNameByCustomerId(transaction.getGateEntryTransaction().getCustomerId());
        String supplierNameBySupplierIdsearchField = supplierMasterRepository.findSupplierNameBySupplierId(transaction.getGateEntryTransaction().getSupplierId());
        String transporterNameByTransporterId = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getGateEntryTransaction().getTransporterId());
            WeighmentTransactionResponse weighmentTransactionResponse = new WeighmentTransactionResponse();
            weighmentTransactionResponse.setTicketNo(String.valueOf(transaction.getGateEntryTransaction().getTicketNo()));
            weighmentTransactionResponse.setWeighmentNo(String.valueOf(transaction.getWeighmentNo()));
            weighmentTransactionResponse.setVehicleFitnessUpTo(byId.getVehicleFitnessUpTo());
            weighmentTransactionResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());
            weighmentTransactionResponse.setCustomerName(customerNameByCustomerId != null ? customerNameByCustomerId : "");
            weighmentTransactionResponse.setSupplierName(supplierNameBySupplierIdsearchField != null ? supplierNameBySupplierIdsearchField : "");
            weighmentTransactionResponse.setVehicleNo(byId.getVehicleNo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            weighmentTransactionResponse.setVehicleIn(transaction.getGateEntryTransaction().getVehicleIn().format(formatter));
            weighmentTransactionResponse.setTransactionDate(transaction.getGateEntryTransaction().getTransactionDate());
            TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getGateEntryTransaction().getTicketNo(), "GWT");
            TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getGateEntryTransaction().getTicketNo(), "TWT");
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
            if (transaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
                String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId(transaction.getGateEntryTransaction().getMaterialId());
                weighmentTransactionResponse.setGrossWeight(String.valueOf(transaction.getTemporaryWeight()) != null ? String.valueOf(transaction.getTemporaryWeight()*1000): "");
                weighmentTransactionResponse.setTareWeight(String.valueOf(transaction.getTareWeight()) != null ? String.valueOf(transaction.getTareWeight()*1000): "");
                weighmentTransactionResponse.setMaterialName(materialNameByMaterialId != null ? materialNameByMaterialId : "");
            } else {
                String productNameByProductId = productMasterRepository.findProductNameByProductId(transaction.getGateEntryTransaction().getMaterialId());
                weighmentTransactionResponse.setTareWeight(String.valueOf(transaction.getTemporaryWeight()) != null ? String.valueOf(transaction.getTemporaryWeight()*1000): "");
                weighmentTransactionResponse.setGrossWeight(String.valueOf(transaction.getGrossWeight()) != null ? String.valueOf(transaction.getGrossWeight()*1000): "");
                weighmentTransactionResponse.setMaterialName(productNameByProductId != null ? productNameByProductId : "");
            }
            weighmentTransactionResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());
            weighmentTransactionResponse.setNetWeight(String.valueOf(transaction.getNetWeight()) != null ? String.valueOf(transaction.getNetWeight()*1000) : "");
            weighmentTransactionResponse.setTransporterName(transporterNameByTransporterId != null ? transporterNameByTransporterId : "");
            return weighmentTransactionResponse;
    }

    @Override
    public WeighbridgePageResponse getAllBySearchFieldsForInprocessTransaction(WeighbridgeOperatorSearchCriteria criteria, Pageable pageable, String userId) {
        UserMaster byId = userMasterRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found with"+userId));
        criteria.setSiteId(byId.getSite().getSiteId());
        criteria.setCompanyId(byId.getCompany().getCompanyId());
        criteria.setUserId(userId);
        GateEntryTransactionSpecification specification = new GateEntryTransactionSpecification(criteria,vehicleMasterRepository,materialMasterRepository,transporterMasterRepository,productMasterRepository,supplierMasterRepository,customerMasterRepository,weighmentTransactionRepository);
        Specification<GateEntryTransaction> netWeightNullSpec = specification.netWeightZero();
        Specification<GateEntryTransaction> combinedSpec = Specification.where(specification).and(netWeightNullSpec);
        Page<GateEntryTransaction> pageResult = gateEntryTransactionRepository.findAll(combinedSpec,pageable);
        List<WeighmentTransactionResponse> responses = pageResult.stream()
                .map(this::mapToInProcessResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        WeighbridgePageResponse response = new WeighbridgePageResponse();
        response.setWeighmentTransactionResponses(responses);
        response.setTotalPages((long) pageResult.getTotalPages());
        response.setTotalElements(pageResult.getTotalElements());
        return response;
    }

    private WeighmentTransactionResponse mapToInProcessResponse(GateEntryTransaction transaction){
        VehicleMaster byId = vehicleMasterRepository.findById(transaction.getVehicleId()).get();
        String customerNameByCustomerId = customerMasterRepository.findCustomerNameByCustomerId(transaction.getCustomerId());
        String supplierNameBySupplierIdsearchField = supplierMasterRepository.findSupplierNameBySupplierId(transaction.getSupplierId());
        String transporterNameByTransporterId = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
        WeighmentTransaction byId1 = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(transaction.getTicketNo());
        WeighmentTransactionResponse weighmentTransactionResponse = new WeighmentTransactionResponse();
        weighmentTransactionResponse.setTicketNo(String.valueOf(transaction.getTicketNo()));
        weighmentTransactionResponse.setVehicleFitnessUpTo(byId.getVehicleFitnessUpTo());
        weighmentTransactionResponse.setTransactionType(transaction.getTransactionType());
        weighmentTransactionResponse.setCustomerName(customerNameByCustomerId != null ? customerNameByCustomerId : "");
        weighmentTransactionResponse.setSupplierName(supplierNameBySupplierIdsearchField != null ? supplierNameBySupplierIdsearchField : "");
        weighmentTransactionResponse.setVehicleNo(byId.getVehicleNo());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        weighmentTransactionResponse.setVehicleIn(transaction.getVehicleIn().format(formatter));
        weighmentTransactionResponse.setTransactionDate(transaction.getTransactionDate());
        TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "GWT");
        TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode(transaction.getTicketNo(), "TWT");
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
        if (transaction.getTransactionType().equalsIgnoreCase("Inbound")) {
            System.out.println("===============");
            String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());

                weighmentTransactionResponse.setWeighmentNo(byId1!=null?String.valueOf(byId1.getWeighmentNo()):"");
                weighmentTransactionResponse.setGrossWeight(byId1!=null?String.valueOf(byId1.getTemporaryWeight()*1000):"");
                weighmentTransactionResponse.setTareWeight(byId1!=null?String.valueOf(byId1.getTareWeight()*1000):"");


            weighmentTransactionResponse.setMaterialName(materialNameByMaterialId != null ? materialNameByMaterialId : "");
        } else {
            String productNameByProductId = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
                weighmentTransactionResponse.setWeighmentNo(byId1!=null?String.valueOf(byId1.getWeighmentNo()):"");
                weighmentTransactionResponse.setTareWeight(byId1!=null?String.valueOf(byId1.getTemporaryWeight()*1000):"");
                weighmentTransactionResponse.setGrossWeight(byId1!=null?String.valueOf(byId1.getGrossWeight()*1000):"");


            weighmentTransactionResponse.setMaterialName(productNameByProductId != null ? productNameByProductId : "");
        }
        weighmentTransactionResponse.setTransactionType(transaction.getTransactionType());
        weighmentTransactionResponse.setNetWeight(byId1 != null ? String.valueOf(byId1.getNetWeight()*1000) : "");
        weighmentTransactionResponse.setTransporterName(transporterNameByTransporterId != null ? transporterNameByTransporterId : "");
        return weighmentTransactionResponse;
    }
}