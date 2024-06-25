package com.weighbridge.management.services.impl;


import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.entities.SupplierMaster;

import com.weighbridge.admin.entities.QualityRangeMaster;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.ProductMasterRepository;
import com.weighbridge.admin.repsitories.QualityRangeMasterRepository;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.admin.repsitories.StatusCodeMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.repositories.GateEntryTransactionRepository;
import com.weighbridge.management.payload.AllTransactionResponse;
import com.weighbridge.management.payload.CoalMoisturePercentageRequest;
import com.weighbridge.management.payload.CoalMoisturePercentageResponse;
import com.weighbridge.management.payload.ManagementGateEntryList;
import com.weighbridge.management.payload.ManagementGateEntryTransactionResponse;
import com.weighbridge.management.payload.ManagementGateEntryTransactionSpecification;
import com.weighbridge.management.payload.ManagementPayload;
import com.weighbridge.management.payload.ManagementQualityDashboardResponse;
import com.weighbridge.management.payload.MaterialProductDataResponse;
import com.weighbridge.management.dtos.WeightResponseForGraph;
import com.weighbridge.management.payload.MaterialProductQualityResponse;
import com.weighbridge.management.services.ManagementDashboardService;
import com.weighbridge.qualityuser.entites.QualityTransaction;
import com.weighbridge.qualityuser.repository.QualityTransactionRepository;
import com.weighbridge.SalesManagement.repositories.SalesProcessRepository;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.gateuser.payloads.GateEntryTransactionSpecification;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;


import com.weighbridge.weighbridgeoperator.entities.VehicleTransactionStatus;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.repositories.VehicleTransactionStatusRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    private VehicleTransactionStatusRepository vehicleTransactionStatusRepository;

    @Autowired
    private SiteMasterRepository siteMasterRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private QualityTransactionRepository qualityTransactionRepository;

    @Autowired
    private ManagementGateEntryTransactionSpecification managementGateEntryTransactionSpecification;

    @Autowired
    private QualityRangeMasterRepository qualityRangeMasterRepository;

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
     * @param managementPayload
     * @return
     */
    @Override
    public AllTransactionResponse getAllTransactionResponse(ManagementPayload managementPayload, String transactionType) {
        if (managementPayload.getCompanyName() == null && managementPayload.getSiteName() == null) {
            throw new ResourceNotFoundException("Select proper site And Company.");
        }
        String companyIdByCompanyName = companyMasterRepository.findCompanyIdByCompanyName(managementPayload.getCompanyName());
        String[] site = managementPayload.getSiteName().split(",");
        String siteIdBySiteName = siteMasterRepository.findSiteIdBySiteName(site[0], site[1]);
        Long gateEntry, gateExit, tareWeight, grossWeight, quality;
        if (transactionType.equalsIgnoreCase("Inbound")) {

            gateEntry = gateEntryTransactionRepository.countGateEntryWithDate("Inbound", managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            gateExit = gateEntryTransactionRepository.countGateExitWithDate("Inbound", managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            tareWeight = weighmentTransactionRepository.countCompletedInboundTareWeights(managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            grossWeight = weighmentTransactionRepository.countCompletedGrossWeightsInbound(managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            quality = qualityTransactionRepository.countInboundQuality("Inbound", managementPayload.getFromDate(), managementPayload.getToDate(), siteIdBySiteName, companyIdByCompanyName);
        } else {
            gateEntry = gateEntryTransactionRepository.countGateEntryWithDate("Outbound", managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            gateExit = gateEntryTransactionRepository.countGateExitWithDate("Outbound", managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            tareWeight = weighmentTransactionRepository.countCompletedOutboundTareWeights(managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            grossWeight = weighmentTransactionRepository.countCompletedGrossWeightsOutbound(managementPayload.getFromDate(), managementPayload.getToDate(), companyIdByCompanyName, siteIdBySiteName);
            quality = qualityTransactionRepository.countInboundQuality("Outbound", managementPayload.getFromDate(), managementPayload.getToDate(), siteIdBySiteName, companyIdByCompanyName);

        }
        AllTransactionResponse allTransactionResponse = new AllTransactionResponse();
        allTransactionResponse.setNoOfQualityTransaction(quality);
        allTransactionResponse.setNoOfGateExit(gateExit);
        allTransactionResponse.setNoOfGateEntry(gateEntry);
        allTransactionResponse.setNoOfTareWeight(tareWeight);
        allTransactionResponse.setNoOfGrossWeight(grossWeight);
        return allTransactionResponse;
    }

    @Override
    public List<ManagementQualityDashboardResponse> getGoodOrBadQualities(ManagementPayload managementRequest, String transactionType, String qualityType) {
        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(managementRequest.getCompanyName());
        String[] siteInfoParts = managementRequest.getSiteName().split(",", 2);
        String siteName = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts.length == 2 ? siteInfoParts[1].trim() : "";
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);

        List<ManagementQualityDashboardResponse> responseList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<QualityTransaction> qualityTransactions = qualityTransactionRepository.findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(companyId, siteMaster.getSiteId(), date);
            for (QualityTransaction transaction : qualityTransactions) {

                ManagementQualityDashboardResponse managementQualityDashboardResponse = new ManagementQualityDashboardResponse();
                managementQualityDashboardResponse.setTicketNo(transaction.getGateEntryTransaction().getTicketNo());
                managementQualityDashboardResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());

                if (transaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
                    SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(transaction.getGateEntryTransaction().getSupplierId());
                    managementQualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                    managementQualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());
                } else {
                    CustomerMaster customerMaster = customerMasterRepository.findByCustomerId(transaction.getGateEntryTransaction().getCustomerId());
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

                // Format the date before adding to the response list
                String formattedDate = date.format(formatter);
                managementQualityDashboardResponse.setTransactionDate(formattedDate); // Assuming you have a setTransactionDate method in your response class

                responseList.add(managementQualityDashboardResponse);
            }
        }
        System.out.println("total responses:" + responseList.size());
        return responseList;
    }



    @Override
    public List<ManagementQualityDashboardResponse> getGoodQualities(ManagementPayload managementRequest, String transactionType) {
        return getQualitiesByType(managementRequest, transactionType, true);
    }

    @Override
    public List<ManagementQualityDashboardResponse> getBadQualities(ManagementPayload managementRequest, String transactionType) {
        return getQualitiesByType(managementRequest, transactionType, false);
    }

    @Override
    public ManagementQualityDashboardResponse searchByTicketNo(String ticketNo, String companyName, String siteName) {
        String companyId = companyMasterRepository.findCompanyIdByCompanyName(companyName);

        String[] siteInfoParts = siteName.split(",", 2);
        String siteNamePart = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts.length == 2 ? siteInfoParts[1].trim() : "";

        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(siteNamePart, siteAddress);

        List<QualityTransaction> qualityTransactions = qualityTransactionRepository.findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(ticketNo, companyId, LocalDate.parse(siteMaster.getSiteId()));

        if (qualityTransactions.isEmpty()) {
            throw new ResourceNotFoundException("Quality Transaction", "ticketNo", ticketNo);
        }

        QualityTransaction transaction = qualityTransactions.get(0);

        ManagementQualityDashboardResponse managementQualityDashboardResponse = new ManagementQualityDashboardResponse();
        managementQualityDashboardResponse.setTicketNo(transaction.getGateEntryTransaction().getTicketNo());
        managementQualityDashboardResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());

        if (transaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
            SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(transaction.getGateEntryTransaction().getSupplierId());
            managementQualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
            managementQualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());
        } else {
            CustomerMaster customerMaster = customerMasterRepository.findByCustomerId(transaction.getGateEntryTransaction().getCustomerId());
            managementQualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
            managementQualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine2());
        }

        boolean isGoodQuality = transaction.getIsQualityGood() != null && transaction.getIsQualityGood();
        managementQualityDashboardResponse.setQualityType(isGoodQuality ? "Good" : "Bad");

        managementQualityDashboardResponse.setVehicleNo(vehicleMasterRepository.findVehicleNoById(transaction.getGateEntryTransaction().getVehicleId()));
        managementQualityDashboardResponse.setProductOrMaterialType(transaction.getGateEntryTransaction().getMaterialType());
        managementQualityDashboardResponse.setProductOrMaterialName(getMaterialOrProductName(transaction.getGateEntryTransaction()));

        String formattedDate = transaction.getGateEntryTransaction().getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        managementQualityDashboardResponse.setTransactionDate(formattedDate);

        return managementQualityDashboardResponse;
    }

    private List<ManagementQualityDashboardResponse> getQualitiesByType(ManagementPayload managementRequest, String transactionType, boolean isGoodQuality) {
        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = startDate;

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(managementRequest.getCompanyName());

        String[] siteInfoParts = managementRequest.getSiteName().split(",", 2);
        String siteName = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts.length == 2 ? siteInfoParts[1].trim() : "";
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);

        List<ManagementQualityDashboardResponse> responseList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<QualityTransaction> qualityTransactions = qualityTransactionRepository.findByGateEntryTransactionCompanyIdAndSiteIdAndTransactionDate(companyId, siteMaster.getSiteId(), date);
            for (QualityTransaction transaction : qualityTransactions) {
                // Filtering based on quality type
                boolean isTransactionGoodQuality = transaction.getIsQualityGood() != null && transaction.getIsQualityGood();
                if (isTransactionGoodQuality == isGoodQuality) {
                    ManagementQualityDashboardResponse managementQualityDashboardResponse = new ManagementQualityDashboardResponse();
                    managementQualityDashboardResponse.setTicketNo(transaction.getGateEntryTransaction().getTicketNo());
                    managementQualityDashboardResponse.setTransactionType(transaction.getGateEntryTransaction().getTransactionType());

                    if (transaction.getGateEntryTransaction().getTransactionType().equalsIgnoreCase("Inbound")) {
                        SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(transaction.getGateEntryTransaction().getSupplierId());
                        if (supplierMaster != null) {
                            managementQualityDashboardResponse.setSupplierOrCustomerName(supplierMaster.getSupplierName());
                            managementQualityDashboardResponse.setSupplierOrCustomerAddress(supplierMaster.getSupplierAddressLine1() + "," + supplierMaster.getSupplierAddressLine2());
                        }
                    } else {
                        CustomerMaster customerMaster = customerMasterRepository.findByCustomerId(transaction.getGateEntryTransaction().getCustomerId());
                        if (customerMaster != null) {
                            managementQualityDashboardResponse.setSupplierOrCustomerName(customerMaster.getCustomerName());
                            managementQualityDashboardResponse.setSupplierOrCustomerAddress(customerMaster.getCustomerAddressLine2());
                        }
                    }
                    managementQualityDashboardResponse.setVehicleNo(vehicleMasterRepository.findVehicleNoById(transaction.getGateEntryTransaction().getVehicleId()));
                    managementQualityDashboardResponse.setProductOrMaterialType(transaction.getGateEntryTransaction().getMaterialType());
                    managementQualityDashboardResponse.setProductOrMaterialName(getMaterialOrProductName(transaction.getGateEntryTransaction()));
                    managementQualityDashboardResponse.setQualityType(isGoodQuality ? "Good" : "Bad");

                    responseList.add(managementQualityDashboardResponse);
                }
            }
        }
        return responseList;
    }
    @Override

    public ManagementGateEntryList gateEntryList(Integer ticketNo, String vehicleNo, LocalDate date, String supplierName, String transactionType, Pageable pageable, String vehicleStatus, String company, String site) {
        // Fetch company ID
        String companyId = companyMasterRepository.findCompanyIdByCompanyName(company);
        if (companyId == null) {
            throw new IllegalArgumentException("Company not found for the provided name: " + company);
        }

        // Extract site name and address
        String[] siteInfoParts = site.split(",", 2);
        String siteName = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts.length == 2 ? siteInfoParts[1].trim() : "";

        // Fetch site ID
        String siteId = siteMasterRepository.findSiteIdBySiteNameAndSiteAddressAndCompanyId(siteName, siteAddress, companyId);
        if (siteId == null) {
            throw new IllegalArgumentException("Site not found for the provided name and address: " + siteName + ", " + siteAddress);
        }

        // Fetch GateEntryTransaction data using specifications and pagination
        Page<GateEntryTransaction> gateEntryTransactionPage = gateEntryTransactionRepository.findAll(
                managementGateEntryTransactionSpecification.getTransactions(ticketNo, vehicleNo, date, supplierName, transactionType, vehicleStatus)
                        .and(managementGateEntryTransactionSpecification.filterBySiteAndCompany(siteId, companyId)), pageable);

        // Map GateEntryTransaction to ManagementGateEntryTransactionResponse
        List<ManagementGateEntryTransactionResponse> transactionResponses = gateEntryTransactionPage.getContent().stream()
                .map(transaction -> {
                    ManagementGateEntryTransactionResponse response = new ManagementGateEntryTransactionResponse();
                    mapGateEntryTransactionToResponse(transaction, response);
                    return response;
                })
                .collect(Collectors.toList());

        // Populate ManagementGateEntryList with the mapped transactions and pagination info
        ManagementGateEntryList managementGateEntryList = new ManagementGateEntryList();
        managementGateEntryList.setTransactions(transactionResponses);
        managementGateEntryList.setTotalPages(gateEntryTransactionPage.getTotalPages());
        managementGateEntryList.setTotalElements(gateEntryTransactionPage.getTotalElements());

        return managementGateEntryList;
    }


    // This method maps a single GateEntryTransaction to ManagementGateEntryTransactionResponse
    private void mapGateEntryTransactionToResponse(GateEntryTransaction transaction, ManagementGateEntryTransactionResponse response) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Set common transaction details
        response.setTicketNo(transaction.getTicketNo());
        response.setMaterialType(transaction.getMaterialType());
        response.setPoNo(transaction.getPoNo());
        response.setTpNo(transaction.getTpNo());
        response.setChallanNo(transaction.getChallanNo());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setChallanDate(transaction.getChallanDate());
        response.setTransactionType(transaction.getTransactionType());

        Object[] vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId = vehicleMasterRepository.findDistinctVehicleInfoByVehicleId(transaction.getVehicleId());

        // Set transporter name
        String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(transaction.getTransporterId());
        response.setTransporter(transporterName);

        // Set timestamps for vehicle in and out
        if (transaction.getVehicleIn() != null) {
            response.setVehicleIn(transaction.getVehicleIn().format(formatter));
        }
        if (transaction.getVehicleOut() != null) {
            response.setVehicleOut(transaction.getVehicleOut().format(formatter));
        }

        if ("Inbound".equals(transaction.getTransactionType())) {
            Object[] supplierNameBySupplierId = supplierMasterRepository.findSupplierNameAndAddressBySupplierId(transaction.getSupplierId());
            // Inbound transaction
            Object[] supplierInfo = (Object[]) supplierNameBySupplierId[0];
            if (supplierInfo != null && supplierInfo.length >= 2) {
                String supplierName = (String) supplierInfo[0];
                String supplierAddress = (String) supplierInfo[1];
                response.setSupplier(supplierName);
                response.setSupplierAddress(supplierAddress);
            }
            String materialName = materialMasterRepository.findMaterialNameByMaterialId(transaction.getMaterialId());
            response.setMaterial(materialName);
        } else if ("Outbound".equals(transaction.getTransactionType())) {
            Object[] customerNameByCustomerId = customerMasterRepository.findCustomerNameAndAddressBycustomerId(transaction.getCustomerId());
            // Outbound transaction
            Object[] customerInfo = (Object[]) customerNameByCustomerId[0];
            if (customerInfo != null && customerInfo.length >= 2) {
                String customerName = (String) customerInfo[0];
                String customerAddress = (String) customerInfo[1];
                response.setCustomer(customerName);
                response.setCustomerAddress(customerAddress);
            }
            String materialName = productMasterRepository.findProductNameByProductId(transaction.getMaterialId());
            response.setMaterial(materialName);
        }
        Object[] vehicleInfo = (Object[]) vehicleNoAndVehicleTypeAndVehicleWheelsNoByVehicleId[0];
        if (vehicleInfo != null && vehicleInfo.length >= 3) {
            String vehicleNoGet = (String) vehicleInfo[0];
            String vehicleType = (String) vehicleInfo[1];
            Integer vehicleWheelsNo = (Integer) vehicleInfo[2];
            response.setVehicleNo(vehicleNoGet);
            response.setVehicleType(vehicleType);
            response.setVehicleWheelsNo(vehicleWheelsNo);
            response.setTpNetWeight(transaction.getSupplyConsignmentWeight());
        } else {
            // Handle case where vehicle info is not available
            response.setVehicleNo(null);
            response.setVehicleType(null);
            response.setVehicleWheelsNo(null);
        }
        // Check if vehicle out transaction log exists
        if (transaction.getVehicleIn() != null) {
            // Vehicle out transaction log exists
            // Process the vehicle out data
            response.setVehicleIn(transaction.getVehicleIn().format(formatter));
        }
        // Check if vehicle out transaction log exists
        if (transaction.getVehicleOut() != null) {
            // Vehicle out transaction log exists
            // Process the vehicle out data
            response.setVehicleOut(transaction.getVehicleOut().format(formatter));
        }
        // Fetch and set vehicle status
        VehicleTransactionStatus vehicleStatus = vehicleTransactionStatusRepository.findByTicketNo(transaction.getTicketNo());
        if (vehicleStatus != null) {
            response.setCurrentStatus(vehicleStatus.getStatusCode());
        } else {
            response.setCurrentStatus(null);
        }
        // Fetch and set weighment details
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(transaction.getTicketNo());
        if (weighmentTransaction != null) {
            response.setGrossWeight(weighmentTransaction.getGrossWeight());
            response.setTareWeight(weighmentTransaction.getTareWeight());
            response.setNetWeight(weighmentTransaction.getNetWeight());
            response.setWeighmentNo(weighmentTransaction.getWeighmentNo());
        }

        // Set quality status
        QualityTransaction qualityTransaction = qualityTransactionRepository.findByTicketNo(transaction.getTicketNo());
        response.setQuality(qualityTransaction != null);
    }


    public MaterialProductQualityResponse getMaterialProductQualities(ManagementPayload managementRequest) {
        LocalDate startDate = managementRequest.getFromDate();
        LocalDate endDate = managementRequest.getToDate();

        MaterialProductQualityResponse response = new MaterialProductQualityResponse();
        response.setCompanyName(managementRequest.getCompanyName());
        response.setSiteName(managementRequest.getSiteName());

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(managementRequest.getCompanyName());
        String[] siteInfoParts = managementRequest.getSiteName().split(",", 2);
        String siteName = siteInfoParts[0].trim();
        String siteAddress = siteInfoParts.length == 2 ? siteInfoParts[1].trim() : "";
        SiteMaster siteMaster = siteMasterRepository.findBySiteNameAndSiteAddress(siteName, siteAddress);

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

    @Override
    public CoalMoisturePercentageResponse getMoisturePercentage(CoalMoisturePercentageRequest coalMoisturePercentageRequest) {
        LocalDate startDate = coalMoisturePercentageRequest.getFromDate();
        LocalDate endDate = coalMoisturePercentageRequest.getToDate();

        String companyId = companyMasterRepository.findCompanyIdByCompanyName(coalMoisturePercentageRequest.getCompanyName());
        String[] site = coalMoisturePercentageRequest.getSiteName().split(",");
        String siteId = siteMasterRepository.findSiteIdBySiteNameAndSiteAddress(site[0], site[1]);

        String[] supplierAddress = coalMoisturePercentageRequest.getSupplierAddress().split(",");
        Long supplierId = supplierMasterRepository.findSupplierIdBySupplierNameAndAddressLines(
                coalMoisturePercentageRequest.getSupplierName(), supplierAddress[0], supplierAddress[1]);

        CoalMoisturePercentageResponse coalMoisturePercentageResponse = new CoalMoisturePercentageResponse();
        coalMoisturePercentageResponse.setMaterialName(coalMoisturePercentageRequest.getMaterialName());
        coalMoisturePercentageResponse.setSupplierName(coalMoisturePercentageRequest.getSupplierName());
        coalMoisturePercentageResponse.setSupplierAddress(coalMoisturePercentageRequest.getSupplierAddress());

        List<CoalMoisturePercentageResponse.MoisturePercentageData> moisturePercentageDataList = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Integer> gateEntryTransactionTicketNos = gateEntryTransactionRepository.findTicketNosByCompanyIdAndSiteIdAndSupplierIdAndTransactionDate(
                    companyId, siteId, supplierId, date);

            List<QualityTransaction> qualityTransactions = qualityTransactionRepository.findByGateEntryTransactionTicketNoIn(gateEntryTransactionTicketNos);

            double totalMoisturePercentageSum = 0;
            int count = 0;

            for (QualityTransaction qualityTransaction : qualityTransactions) {
                if (qualityTransaction != null) {
                    String[] qualityRangeIds = qualityTransaction.getQualityRangeId().split(",");
                    String[] qualityValues = qualityTransaction.getQualityValues().split(",");
                    Map<Long, String> qualityParameters = qualityRangeMasterRepository.findAllById(Arrays.stream(qualityRangeIds)
                                    .map(Long::valueOf).collect(Collectors.toList()))
                            .stream().collect(Collectors.toMap(QualityRangeMaster::getQualityRangeId, QualityRangeMaster::getParameterName));

                    for (int i = 0; i < qualityRangeIds.length; i++) {
                        if ("Moisture%".equals(qualityParameters.get(Long.valueOf(qualityRangeIds[i])))) {
                            totalMoisturePercentageSum += Double.valueOf(qualityValues[i]);
                            count++;
                            break;
                        }
                    }
                }
            }

            double averageMoisturePercentage = (count > 0) ? totalMoisturePercentageSum / count : 0.0;
            CoalMoisturePercentageResponse.MoisturePercentageData moisturePercentageData = new CoalMoisturePercentageResponse.MoisturePercentageData();
            moisturePercentageData.setTransactionDate(date);
            moisturePercentageData.setParameterName("Moisture%");
            moisturePercentageData.setMoisturePercentage(averageMoisturePercentage);
            moisturePercentageDataList.add(moisturePercentageData);
        }


        coalMoisturePercentageResponse.setMoisturePercentageData(moisturePercentageDataList);
        return coalMoisturePercentageResponse;
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
            String materialNameByMaterialId;
            if (transactionType.equalsIgnoreCase("Inbound")) {
                materialNameByMaterialId = materialMasterRepository.findMaterialNameByMaterialId((Long) result[1]);
            } else {
                materialNameByMaterialId = productMasterRepository.findProductNameByProductId((Long) result[1]);
            }

            weightResponseForGraph.setMaterialName(materialNameByMaterialId);
            weightResponseForGraph.setTotalQuantity((Double) result[2]);
            weightResponseForGraphs.add(weightResponseForGraph);
        }
        return weightResponseForGraphs;
    }
}