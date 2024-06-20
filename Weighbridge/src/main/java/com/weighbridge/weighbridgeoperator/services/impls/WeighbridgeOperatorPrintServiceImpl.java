package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeOperatorPrint;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighbridgeOperatorPrintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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

    @Autowired
    private CompanyMasterRepository companyMasterRepository;
    @Autowired
    private TransactionLogRepository transactionLogRepository;

    /**
     * @return
     */
    @Override
    public WeighbridgeOperatorPrint getPrintResponse(Integer ticketNo) {
        GateEntryTransaction byTicketNo = gateEntryTransactionRepository.findByTicketNo(ticketNo);
        if (byTicketNo==null){
            throw new ResourceNotFoundException("Ticket not found");
        }
        WeighbridgeOperatorPrint weighbridgeOperatorPrint=new WeighbridgeOperatorPrint();
        weighbridgeOperatorPrint.setTicketNo(byTicketNo.getTicketNo());
        weighbridgeOperatorPrint.setVehicleNo(vehicleMasterRepository.findVehicleNoById(byTicketNo.getVehicleId()));
        if(byTicketNo.getTransactionType().equalsIgnoreCase("Outbound")) {
            weighbridgeOperatorPrint.setProductName(productMasterRepository.findProductNameByProductId(byTicketNo.getMaterialId()));
            weighbridgeOperatorPrint.setCustomerName(customerMasterRepository.findCustomerNameByCustomerId(byTicketNo.getCustomerId()));
            weighbridgeOperatorPrint.setGrossWeight(weighmentTransactionRepository.findByGateEntryTransactionTicketNo(byTicketNo.getTicketNo()).getGrossWeight()*1000);
            weighbridgeOperatorPrint.setTareWeight(weighmentTransactionRepository.findByGateEntryTransactionTicketNo(byTicketNo.getTicketNo()).getTemporaryWeight()*1000);
        }
        else {
            weighbridgeOperatorPrint.setMaterialName(materialMasterRepository.findMaterialNameByMaterialId(byTicketNo.getMaterialId()));
            weighbridgeOperatorPrint.setSupplierName(supplierMasterRepository.findSupplierNameBySupplierId(byTicketNo.getSupplierId()));
            weighbridgeOperatorPrint.setTareWeight(weighmentTransactionRepository.findByGateEntryTransactionTicketNo(byTicketNo.getTicketNo()).getTareWeight()*1000);
           weighbridgeOperatorPrint.setGrossWeight(weighmentTransactionRepository.findByGateEntryTransactionTicketNo(byTicketNo.getTicketNo()).getTemporaryWeight()*1000);
        }
        weighbridgeOperatorPrint.setTransporterName(transporterMasterRepository.findTransporterNameByTransporterId(byTicketNo.getTransporterId()));
        weighbridgeOperatorPrint.setChallanNo(byTicketNo.getTpNo());
        weighbridgeOperatorPrint.setCompanyName(companyMasterRepository.findCompanyNameByCompanyId(byTicketNo.getCompanyId()));
        weighbridgeOperatorPrint.setCompanyAdress(companyMasterRepository.findCompanyAddressByCompanyId(byTicketNo.getCompanyId()));
        TransactionLog gwt = transactionLogRepository.findByTicketNoAndStatusCode(byTicketNo.getTicketNo(), "GWT");
        TransactionLog twt = transactionLogRepository.findByTicketNoAndStatusCode(byTicketNo.getTicketNo(), "TWT");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dateFormat = gwt != null ? gwt.getTimestamp().format(dateFormatter) : "";
        String timeFormat=gwt!=null?gwt.getTimestamp().format(timeFormatter):"";
        weighbridgeOperatorPrint.setGrossWeightDate(dateFormat);
        weighbridgeOperatorPrint.setGrossWeightTime(timeFormat);
        String dateFormat1 = twt != null ? twt.getTimestamp().format(dateFormatter) : "";
        String timeFormat1=twt!=null?twt.getTimestamp().format(timeFormatter):"";
        weighbridgeOperatorPrint.setTareWeightDate(dateFormat1);
        weighbridgeOperatorPrint.setTareWeightTime(timeFormat1);
        weighbridgeOperatorPrint.setNetWeight(weighmentTransactionRepository.findByGateEntryTransactionTicketNo(byTicketNo.getTicketNo()).getNetWeight()*1000);
        System.out.println(weighbridgeOperatorPrint);
        return weighbridgeOperatorPrint;
    }
}