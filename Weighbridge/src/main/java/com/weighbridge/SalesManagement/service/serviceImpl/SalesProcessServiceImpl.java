package com.weighbridge.SalesManagement.service.serviceImpl;

import com.weighbridge.SalesManagement.entities.SalesOrder;
import com.weighbridge.SalesManagement.entities.SalesProcess;
import com.weighbridge.SalesManagement.payloads.SalesDetailBySalePassNo;
import com.weighbridge.SalesManagement.payloads.SalesDetailResponse;
import com.weighbridge.SalesManagement.payloads.SalesProcessRequest;
import com.weighbridge.SalesManagement.payloads.VehicleAndTransporterDetail;
import com.weighbridge.SalesManagement.repositories.SalesOrderRespository;
import com.weighbridge.SalesManagement.repositories.SalesProcessRepository;
import com.weighbridge.SalesManagement.service.SalesProcessService;
import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.admin.entities.MaterialTypeMaster;
import com.weighbridge.admin.entities.TransporterMaster;
import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.MaterialTypeMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Service
public class SalesProcessServiceImpl implements SalesProcessService {

    @Autowired
    SalesProcessRepository salesProcessRepository;

    @Autowired
    SalesOrderRespository salesOrderRespository;

    @Autowired
    VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    TransporterMasterRepository transporterMasterRepository;

    @Autowired
    MaterialMasterRepository productMasterRepository;

    @Autowired
    MaterialTypeMasterRepository materialTypeMasterRepository;

    /**
     * To process sale of the material with vehicle and transporter details
     *
     * @param salesProcessRequest contains vehicles and transporter details with the progressive quantity of product
     * @return
     */
    @Override
    public String addSalesProcess(SalesProcessRequest salesProcessRequest) {
        /*Boolean sales = salesProcessRepository.existsByPurchasePassNo();
        if (!sales) {*/
            SalesProcess process = new SalesProcess();
            process.setPurchaseProcessDate(salesProcessRequest.getPurchaseProcessDate());
            process.setVehicleNo(salesProcessRequest.getVehicleNo());
            process.setConsignmentWeight(salesProcessRequest.getConsignmentWeight());
          //  process.setNetWeight(salesProcessRequest.getNetWeight());

       /* MaterialMaster byMaterialName = productMasterRepository.findByMaterialName(salesProcessRequest.getProductName());
        Boolean material = materialTypeMasterRepository.existsByMaterialMasterMaterialId(byMaterialName.getMaterialId());
        if(!material){
            MaterialTypeMaster materialTypeMaster=new MaterialTypeMaster();
            materialTypeMaster.setMaterialTypeName(salesProcessRequest.getProductType());
            materialTypeMaster.setMaterialMaster(byMaterialName);
            materialTypeMasterRepository.save(materialTypeMaster);
        }*/
            process.setProductName(salesProcessRequest.getProductName());
            process.setProductType(salesProcessRequest.getProductType());
            SalesOrder bySaleOrderNo = salesOrderRespository.findBySaleOrderNo(String.valueOf(salesProcessRequest.getSaleOrderNo()));
        System.out.println(bySaleOrderNo);
            process.setPurchaseSale(bySaleOrderNo);
            process.setTransporterName(salesProcessRequest.getTransporterName());
            process.setSalePassNo(generateSalePassNo(salesProcessRequest.getSaleOrderNo()));
            salesProcessRepository.save(process);

            //Add vehicle and transporter to VehicleMaster and TransporterMaster

 /*           TransporterMaster transporterMaster = transporterMasterRepository.findByTransporterName(salesProcessRequest.getTransporterName());
//            Boolean vehicle = vehicleMasterRepository.existsByVehicleNO(salesProcessRequest.getVehicleNo());
            VehicleMaster vehicleMaster = vehicleMasterRepository.findByVehicleNo(salesProcessRequest.getVehicleNo());
            if (transporterMaster == null && vehicleMaster != null) {
                transporterMaster = new TransporterMaster();
                transporterMaster.setTransporterName(salesProcessRequest.getTransporterName());

                Set<VehicleMaster> vehicleMasterSet = new HashSet<>();
                vehicleMasterSet.add(vehicleMaster);

                transporterMaster.setVehicles(vehicleMasterSet);
                transporterMasterRepository.save(transporterMaster);
            }
            if (transporterMaster != null && vehicleMaster == null) {
                vehicleMaster = new VehicleMaster();
                vehicleMaster.setVehicleNo(salesProcessRequest.getVehicleNo());
                VehicleMaster savedVehicle = vehicleMasterRepository.save(vehicleMaster);
                transporterMaster.addVehicle(savedVehicle);
                transporterMasterRepository.save(transporterMaster);
            }
            if (transporterMaster == null && vehicleMaster == null) {
                vehicleMaster = new VehicleMaster();
                vehicleMaster.setVehicleNo(salesProcessRequest.getVehicleNo());
                VehicleMaster savedVehicle = vehicleMasterRepository.save(vehicleMaster);
                transporterMaster = new TransporterMaster();
                transporterMaster.setTransporterName(salesProcessRequest.getTransporterName());

                Set<VehicleMaster> vehicleMasterSet = new HashSet<>();
                vehicleMasterSet.add(vehicleMaster);

                transporterMaster.setVehicles(vehicleMasterSet);
                transporterMasterRepository.save(transporterMaster);
            }*/

        return "Sales data added successfully";
    }

    /**
     * @param saleOrderNo
     * @return
     */
    @Override
    public List<SalesDetailBySalePassNo> getBySaleOrderNo(String saleOrderNo) {
        List<SalesProcess> byPurchaseSaleSaleOrderNo = salesProcessRepository.findByPurchaseSaleSaleOrderNo(saleOrderNo);
        List<SalesDetailBySalePassNo> salesList=new ArrayList<>();
        for(SalesProcess salesProcess:byPurchaseSaleSaleOrderNo){
            SalesDetailBySalePassNo salesDetailBySalePassNo=new SalesDetailBySalePassNo();
            salesDetailBySalePassNo.setSalePassNo(salesProcess.getSalePassNo());
            salesDetailBySalePassNo.setProductName(salesProcess.getProductName());
            salesDetailBySalePassNo.setVehicleNo(salesProcess.getVehicleNo());
            salesDetailBySalePassNo.setTransporterName(salesProcess.getTransporterName());
            salesDetailBySalePassNo.setConsignmentWeight(salesProcess.getConsignmentWeight());
            salesDetailBySalePassNo.setProductType(salesProcess.getProductType());
            salesList.add(salesDetailBySalePassNo);
        }
        return salesList;
    }


    private String generateSalePassNo(String saleOrderNo) {
        Long count = salesProcessRepository.countByPurchaseSaleSaleOrderNo(saleOrderNo);

        // Increment the count for the current purchase order and format it as a 2-digit string
        String incrementedNumber = String.format("%02d", count + 1);

        String salePassNo = saleOrderNo + "-" + incrementedNumber;
        return salePassNo;
    }
}