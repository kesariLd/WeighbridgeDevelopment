package com.weighbridge.management.services.impl;

import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.ProductMasterRepository;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.services.ManagementDashboardService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagementDashboardServiceImpl implements ManagementDashboardService {

    @Autowired
    private GateEntryTransactionRepository gateEntryTransactionRepository;

    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private SiteMasterRepository siteMasterRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Override
    public MaterialProductDataResponse getMaterialProductBarChartData(ManagementPayload managementRequest) {
        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = managementRequest.getToDate();

        MaterialProductDataResponse response = new MaterialProductDataResponse();
        response.setCompanyName(managementRequest.getCompanyName());
        response.setSiteName(managementRequest.getSiteName());

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(managementRequest.getCompanyName());
        String[] site = managementRequest.getSiteName().split(",");
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(site[0], site[1]);

        List<MaterialProductDataResponse.MaterialProductData> materialProductDataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<WeighmentTransaction> weighmentTransactionList = weighmentTransactionRepository.findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(companyId, siteMaster.getSiteId(), date);

            MaterialProductDataResponse.MaterialProductData materialProductData = new MaterialProductDataResponse.MaterialProductData();
            materialProductData.setTransactionDate(date);

            List<String> allMaterials = materialMasterRepository.findAllMaterialNameByMaterialStatus("ACTIVE");
            List<String> allProducts = productMasterRepository.findAllProductNameByProductStatus("ACTIVE");

            Map<String, Double> materialData = new HashMap<>(); // Use HashMap for dynamic material names
            for (String material : allMaterials) {
                materialData.put(material, 0.0);
            }
            for (String product : allProducts) {
                materialData.put(product, 0.0);
            }
            for (WeighmentTransaction weighmentTransaction : weighmentTransactionList) {
                if (weighmentTransaction.getNetWeight() != 0.0) {
                    String transactionType = weighmentTransaction.getGateEntryTransaction().getTransactionType();
                    long materialOrProductId = weighmentTransaction.getGateEntryTransaction().getMaterialId();

                    String materialName;
                    if (transactionType.equals("Inbound")) {
                        materialName = materialMasterRepository.findMaterialNameByMaterialId(materialOrProductId);
                    } else {
                        materialName = productMasterRepository.findProductNameByProductId(materialOrProductId);
                    }

                    materialData.put(materialName, materialData.get(materialName) + weighmentTransaction.getNetWeight());
                }
            }
            materialProductData.setMaterialData(materialData);
            materialProductDataList.add(materialProductData);
        }
        response.setMaterialProductData(materialProductDataList);
        return response;
    }


  /*  public Long getInboundCount(ManagementPayload managementPayload){
        gateEntryTransactionRepository.countInbounddetails
        return null;
    }*/
}