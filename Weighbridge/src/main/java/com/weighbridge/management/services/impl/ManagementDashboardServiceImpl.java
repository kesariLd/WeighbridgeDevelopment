package com.weighbridge.management.services.impl;

import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.entities.SupplierMaster;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.ManagementQualityDashboardResponse;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.payload.MaterialProductQualityResponse;
import com.weighbridge.management.services.ManagementDashboardService;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.exception.ResourceNotFoundException;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
    private HttpServletRequest httpServletRequest;

    @Autowired
    private QualityTransactionRepository qualityTransactionRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;


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

    @Override
    public List<ManagementQualityDashboardResponse> getGoodOrBadQualities(ManagementPayload managementRequest, String transactionType, String qualityType) {
        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = managementRequest.getToDate();

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(managementRequest.getCompanyName());
        String[] site = managementRequest.getSiteName().split(",");
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(site[0], site[1]);
        System.out.println(site[0] + "," + site[1]);

        List<ManagementQualityDashboardResponse> responseList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<QualityTransaction> qualityTransactions = qualityTransactionRepository.findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(companyId, siteMaster.getSiteId(), date);
            for (QualityTransaction transaction : qualityTransactions) {
                ManagementQualityDashboardResponse managementQualityDashboardResponse = new ManagementQualityDashboardResponse();
                managementQualityDashboardResponse.setTicketNo(transaction.getGateEntryTransaction().getTicketNo());
                managementQualityDashboardResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());
//                managementQualityDashboardResponse.setSupplierOrCustomerName(getSupplierOrCustomerNameAndAddress(transaction.getGateEntryTransaction()));
//                Object[] supplier = getSupplierOrCustomerName(transaction.getGateEntryTransaction());
//                if (supplier != null && supplier.length == 3) {
//                    managementQualityDashboardResponse.setSupplierOrCustomerName(supplier[0].toString());
//                    managementQualityDashboardResponse.setSupplierOrCustomerAddress(supplier[1] + "," + supplier[2]);
//                } else {
//                    // Handle the case where the returned array has a different length
//                    log.error("Unexpected array length returned from findSupplierNameAndSupplierAddressesBySupplierId");
//                    managementQualityDashboardResponse.setSupplierOrCustomerName(null);
//                    managementQualityDashboardResponse.setSupplierOrCustomerAddress(null);
//                }
                if (transaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
                    SupplierMaster supplierMaster = supplierMasterRepository
                            .findBySupplierId(transaction.getGateEntryTransaction().getSupplierId());
                    managementQualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                    managementQualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());
                } else {
                    CustomerMaster customerMaster = customerMasterRepository
                            .findByCustomerId(transaction.getGateEntryTransaction().getCustomerId());
                    managementQualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                    managementQualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine2());
                }
                boolean isGoodQuality = transaction.getIsQualityGood() != null && transaction.getIsQualityGood();
                if (isGoodQuality) {
                    managementQualityDashboardResponse.setQualityType("Good");
                } else {
                    managementQualityDashboardResponse.setQualityType("Bad");
                }
                managementQualityDashboardResponse.setVehicleNo(vehicleMasterRepository.findVehicleNoById(transaction.getGateEntryTransaction().getVehicleId()));
                managementQualityDashboardResponse.setProductOrMaterialType(transaction.getGateEntryTransaction().getMaterialType());
                managementQualityDashboardResponse.setProductOrMaterialName(getMaterialOrProductName(transaction.getGateEntryTransaction()));
                responseList.add(managementQualityDashboardResponse);
            }
        }
        return responseList;
    }


    @Override
    public MaterialProductQualityResponse getMaterialProductQualities(ManagementPayload managementRequest) {
        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = managementRequest.getToDate();

        MaterialProductQualityResponse response = new MaterialProductQualityResponse();
        response.setCompanyName(managementRequest.getCompanyName());
        response.setSiteName(managementRequest.getSiteName());

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(managementRequest.getCompanyName());
        String[] site = managementRequest.getSiteName().split(",");
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(site[0], site[1]);

        List<MaterialProductQualityResponse.MaterialProductQualityData> materialProductQualityDataList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<QualityTransaction> qualityTransactions = qualityTransactionRepository.findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(companyId, siteMaster.getSiteId(), date);
            MaterialProductQualityResponse.MaterialProductQualityData materialProductQualityData = new MaterialProductQualityResponse.MaterialProductQualityData();
            materialProductQualityData.setTransactionDate(date);

            System.out.println("Date: " + date + ", Quality Transactions: " + qualityTransactions.size());

            Map<String, List<QualityTransaction>> groupedTransaction = qualityTransactions.stream().collect(Collectors.groupingBy(transaction -> {

                        GateEntryTransaction gateEntryTransaction = gateEntryTransactionRepository.findByTicketNo(transaction.getGateEntryTransaction().getTicketNo());
                        return getMaterialOrProductName(gateEntryTransaction);
                    }
            ));

            List<MaterialProductQualityResponse.QualityData> qualityDataList = new ArrayList<>();
            for (Map.Entry<String, List<QualityTransaction>> entry : groupedTransaction.entrySet()) {
                String materialOrProductName = entry.getKey();
                List<QualityTransaction> transactions = entry.getValue();

                long totalTransactions = transactions.size();
                long goodTransactions = transactions.stream()
                        .filter(transaction -> {
                            Boolean isQualityGood = transaction.getIsQualityGood();
                            return isQualityGood != null && isQualityGood;
                        })
                        .count();

                long badTransactions = totalTransactions - goodTransactions;

                double goodPercentage = (double) goodTransactions / totalTransactions * 100;
                double badPercentage = (double) badTransactions / totalTransactions * 100;

                MaterialProductQualityResponse.QualityData qualityData = new MaterialProductQualityResponse.QualityData();
                qualityData.setMaterialOrProductName(materialOrProductName);
                qualityData.setGoodPercentage(goodPercentage);
                qualityData.setBadPercentage(badPercentage);

                qualityDataList.add(qualityData);

            }
            materialProductQualityData.setQualityData(qualityDataList);
            materialProductQualityDataList.add(materialProductQualityData);

        }
        response.setMaterialProductQualityData(materialProductQualityDataList);

        return response;
    }

    private String getMaterialOrProductName(GateEntryTransaction gateEntryTransaction) {
        if (gateEntryTransaction == null) {
            return "materialOrProductName is not found";
        }
        String materialName;
        if ("Inbound".equalsIgnoreCase(gateEntryTransaction.getTransactionType())) {
            materialName = materialMasterRepository.findMaterialNameByMaterialId(gateEntryTransaction.getMaterialId());
        } else {
            materialName = productMasterRepository.findProductNameByProductId(gateEntryTransaction.getMaterialId());
        }
        if (materialName == null) {
            return "materialOrProductName is not found";
        }
        System.out.println("material name:" + materialName);
        return materialName;
    }

    private Object[] getSupplierOrCustomerName(GateEntryTransaction gateEntryTransaction) {
        if (gateEntryTransaction == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket is not found");
        }
        if ("Inbound".equalsIgnoreCase(gateEntryTransaction.getTransactionType())) {
            return supplierMasterRepository.findSupplierNameAndSupplierAddressesBySupplierId(gateEntryTransaction.getSupplierId());
        } else {
            return customerMasterRepository.findCustomerNameAndCustomerAddressesByCustomerId(gateEntryTransaction.getCustomerId());
        }
    }




  /*  public Long getInboundCount(ManagementPayload managementPayload){
        gateEntryTransactionRepository.countInbounddetails
        return null;
    }*/

    @Override
    public List<WeightResponseForGraph> getQtyResponseInGraph(ManagementPayload managementPayload, String transactionType) {
        if (managementPayload.getFromDate() == null || managementPayload.getToDate() == null) {
            LocalDate today = LocalDate.now();
            managementPayload.setFromDate(today);
            managementPayload.setToDate(today);
        }
        String[] site = managementPayload.getSiteName().split(",");
        String siteIdBySiteName = siteMasterRepository.findSiteIdBySiteName(site[0], site[1]);
        String companyIdByCompanyName = companyMasterRepository.findCompanyIdByCompanyName(managementPayload.getCompanyName());
        List<Object[]> totalNetWeightByTransactionDateAndMaterialId = weighmentTransactionRepository.findTotalNetWeightByTransactionDateAndMaterialId(managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName, transactionType);
        System.out.println("response " + totalNetWeightByTransactionDateAndMaterialId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
        List<WeightResponseForGraph> weightResponseForGraphs = new ArrayList<>();
        for (Object[] result : totalNetWeightByTransactionDateAndMaterialId) {
            WeightResponseForGraph weightResponseForGraph = new WeightResponseForGraph();
            LocalDate date = (LocalDate) result[0];
            weightResponseForGraph.setTransactionDate(date != null ? date.format(formatter) : "");
            String materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId((Long) result[1]);
            weightResponseForGraph.setMaterialName(materialNameByMaterialId);
            weightResponseForGraph.setTotalQuantity((Double) result[2]);
            weightResponseForGraphs.add(weightResponseForGraph);
        }
        return weightResponseForGraphs;
    }
}
