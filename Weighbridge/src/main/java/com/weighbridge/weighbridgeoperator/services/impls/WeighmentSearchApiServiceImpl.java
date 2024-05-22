package com.weighbridge.weighbridgeoperator.services.impls;


import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentSearchApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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


    /**
     * @return
     */
    @Override
    public WeighmentTransactionResponse getByTicketNo(Integer ticketNo) {
        WeighmentTransaction byWeighmentId = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);
        VehicleMaster byId = vehicleMasterRepository.findById(byWeighmentId.getGateEntryTransaction().getVehicleId()).get();
        String customerNameByCustomerId = customerMasterRepository.findCustomerNameByCustomerId(byWeighmentId.getGateEntryTransaction().getCustomerId());
        String supplierNameBySupplierIdsearchField = supplierMasterRepository.findSupplierNameBySupplierId(byWeighmentId.getGateEntryTransaction().getSupplierId());
        String transporterNameByTransporterId = transporterMasterRepository.findTransporterNameByTransporterId(byWeighmentId.getGateEntryTransaction().getTransporterId());

        if(byWeighmentId==null){
            throw new RuntimeException("ticket not found with ticketNo "+ticketNo);
        }
        else {
            WeighmentTransactionResponse weighmentTransactionResponse=new WeighmentTransactionResponse();
            weighmentTransactionResponse.setTicketNo(String.valueOf(byWeighmentId.getGateEntryTransaction().getTicketNo()));
            weighmentTransactionResponse.setWeighmentNo(String.valueOf(byWeighmentId.getWeighmentNo()));
            weighmentTransactionResponse.setVehicleFitnessUpTo(byId.getVehicleFitnessUpTo());
            weighmentTransactionResponse.setTransactionType(byWeighmentId.getGateEntryTransaction().getTransactionType());
            weighmentTransactionResponse.setCustomerName(customerNameByCustomerId!=null?customerNameByCustomerId:"");
            weighmentTransactionResponse.setSupplierName(supplierNameBySupplierIdsearchField!=null?supplierNameBySupplierIdsearchField:"");
            weighmentTransactionResponse.setVehicleNo(byId.getVehicleNo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            weighmentTransactionResponse.setVehicleIn(byWeighmentId.getGateEntryTransaction().getVehicleIn().format(formatter));
            weighmentTransactionResponse.setTransactionDate(byWeighmentId.getGateEntryTransaction().getTransactionDate());
            TransactionLog byTicketNo = transactionLogRepository.findByTicketNoAndStatusCode(byWeighmentId.getGateEntryTransaction().getTicketNo(), "GWT");
            TransactionLog byTicketNo2 = transactionLogRepository.findByTicketNoAndStatusCode(byWeighmentId.getGateEntryTransaction().getTicketNo(), "TWT");
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
            if(byWeighmentId.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
                String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId(byWeighmentId.getGateEntryTransaction().getMaterialId());
                weighmentTransactionResponse.setGrossWeight(String.valueOf(byWeighmentId.getTemporaryWeight())!=null? String.valueOf(byWeighmentId.getTemporaryWeight())+"/"+restTimeStamp :"");
                weighmentTransactionResponse.setTareWeight(String.valueOf(byWeighmentId.getTareWeight())!=null? String.valueOf(byWeighmentId.getTareWeight())+"/"+restTimeStamp1 :"");
                weighmentTransactionResponse.setMaterialName(materialNameByMaterialId!=null?materialNameByMaterialId:"");
            }
            else {
                String productNameByProductId = productMasterRepository.findProductNameByProductId(byWeighmentId.getGateEntryTransaction().getMaterialId());
                weighmentTransactionResponse.setTareWeight(String.valueOf(byWeighmentId.getTemporaryWeight())!=null? String.valueOf(byWeighmentId.getTemporaryWeight())+"/"+restTimeStamp1 :"");
                weighmentTransactionResponse.setGrossWeight(String.valueOf(byWeighmentId.getGrossWeight())!=null? String.valueOf(byWeighmentId.getGrossWeight())+"/"+restTimeStamp :"");
                weighmentTransactionResponse.setMaterialName(productNameByProductId!=null?productNameByProductId:"");
            }
            weighmentTransactionResponse.setTransactionType(byWeighmentId.getGateEntryTransaction().getTransactionType());
            weighmentTransactionResponse.setNetWeight(String.valueOf(byWeighmentId.getNetWeight())!=null?String.valueOf(byWeighmentId.getNetWeight()):"");
            weighmentTransactionResponse.setTransporterName(transporterNameByTransporterId!=null?transporterNameByTransporterId:"");
            return weighmentTransactionResponse;
        }
    }

    /**
     * @param fieldName
     * @return
     */
    @Override
    public List<WeighmentTransactionResponse> getBySearchfield(String fieldName) {

        return null;
    }
}