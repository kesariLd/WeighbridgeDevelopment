package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeOperatorPrint;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighbridgeOperatorPrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeighbridgeOperatorPrintServiceImpl implements WeighbridgeOperatorPrintService {

    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private MaterialMasterRepository  materialMasterRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    /**
     * @return
     */
    @Override
    public WeighbridgeOperatorPrint getPrintResponse(Integer ticketNo) {
        GateEntryTransaction byTicketNo = gateEntryTransactionRepository.findByTicketNo(ticketNo);
        WeighbridgeOperatorPrint weighbridgeOperatorPrint=new WeighbridgeOperatorPrint();
        weighbridgeOperatorPrint.setTicketNo(byTicketNo.getTicketNo());
        weighbridgeOperatorPrint.setVehicleNo(vehicleMasterRepository.findVehicleNoById(byTicketNo.getVehicleId()));
        if(byTicketNo.getTransactionType().equalsIgnoreCase("Outbound")) {
            weighbridgeOperatorPrint.setProductName(productMasterRepository.findProductNameByProductId(byTicketNo.getMaterialId()));
            weighbridgeOperatorPrint.setCustomerName(customerMasterRepository.findCustomerNameByCustomerId(byTicketNo.getCustomerId()));

        }
        else {
            weighbridgeOperatorPrint.setMaterialName(materialMasterRepository.findMaterialNameByMaterialId(byTicketNo.getMaterialId()));
            weighbridgeOperatorPrint.setSupplierName(supplierMasterRepository.findSupplierNameBySupplierId(byTicketNo.getSupplierId()));
            weighbridgeOperatorPrint.setTareWeight(weighmentTransactionRepository.findByGateEntryTransactionTicketNo(byTicketNo.getTicketNo()).getTemporaryWeight());
       //     weighbridgeOperatorPrint.setGrossWeight();
        }
        weighbridgeOperatorPrint.setTransporterName(transporterMasterRepository.findTransporterNameByTransporterId(byTicketNo.getTransporterId()));
        weighbridgeOperatorPrint.setChallanNo(byTicketNo.getTpNo());
        return weighbridgeOperatorPrint;
    }
}