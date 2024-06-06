package com.weighbridge.management.services.impl;

import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.services.ManagementDashboardService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private CustomerMasterRepository customerMasterRepository;
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
    @Override
    public List<Map<String, Object>> managementGateEntryDashboard(ManagementPayload managementRequest) {
        // Validate the request
        if (managementRequest == null || managementRequest.getCompanyName() == null || managementRequest.getSiteName() == null) {
            throw new IllegalArgumentException("Invalid management request: request, company name, or site name is null");
        }

        String companyName = managementRequest.getCompanyName();
        String[] siteInfoParts = managementRequest.getSiteName().split(",", 2);
        String siteName = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts.length == 2 ? siteInfoParts[1].trim() : "";

        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = managementRequest.getToDate();

        // Validate date range
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Invalid date range: startDate and endDate must be valid and endDate should be after startDate");
        }

        // Fetch company ID
        String companyId = companyMasterRepository.findCompanyIdByCompanyName(companyName);
        if (companyId == null) {
            throw new IllegalArgumentException("Company not found for the provided name: " + companyName);
        }

        // Fetch site ID
        String siteIdByFetch = siteMasterRepository.findSiteIdBySiteNameAndSiteAddressAndCompanyId(siteName, siteAddress, companyId);
        if (siteIdByFetch == null) {
            throw new IllegalArgumentException("Site not found for the provided name and address: " + siteName + ", " + siteAddress);
        }

        // Fetch the data from repository
        return gateEntryTransactionRepository.findByTransactionStartDateAndTransactionEndDateCompanySite(startDate, endDate, companyId, siteIdByFetch);
    }


    /**
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<WeightResponseForGraph> getQtyResponseInGraph(ManagementPayload managementPayload) {
        if(managementPayload.getFromDate()==null||managementPayload.getToDate()==null){
            LocalDate today = LocalDate.now();
            managementPayload.setFromDate(today);
            managementPayload.setToDate(today);
        }
        String[] site = managementPayload.getSiteName().split(",");
        String siteIdBySiteName = siteMasterRepository.findSiteIdBySiteName(site[0], site[1]);
        String companyIdByCompanyName = companyMasterRepository.findCompanyIdByCompanyName(managementPayload.getCompanyName());
        List<Object[]> totalNetWeightByTransactionDateAndMaterialId = weighmentTransactionRepository.findTotalNetWeightByTransactionDateAndMaterialId(managementPayload.getFromDate(), managementPayload.getToDate(),companyIdByCompanyName,siteIdBySiteName);
        System.out.println("response "+totalNetWeightByTransactionDateAndMaterialId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
        List<WeightResponseForGraph> weightResponseForGraphs=new ArrayList<>();
        for(Object[] result:totalNetWeightByTransactionDateAndMaterialId) {
            WeightResponseForGraph weightResponseForGraph = new WeightResponseForGraph();
            LocalDate date = (LocalDate) result[0];
            weightResponseForGraph.setTransactionDate(date!=null?date.format(formatter):"");
            String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId((Long) result[1]);
            weightResponseForGraph.setMaterialName(materialNameByMaterialId);
            weightResponseForGraph.setTotalQuantity((Double) result[2]);
            weightResponseForGraphs.add(weightResponseForGraph);
        }
        return weightResponseForGraphs;
    }
}