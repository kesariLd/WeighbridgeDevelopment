package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.admin.entities.CompanyMaster;
import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.MaterialMasterRepository;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.UserMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentPrintResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponseList;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import com.weighbridge.weighbridgeoperator.services.WeighmentReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class WeighmentReportServiceImpl implements WeighmentReportService {

    @Autowired
    private GateEntryTransactionService gateEntryTransactionService;

    @Autowired
    private WeighmentTransactionRepository weighmentTransactionRepository;

    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;

    @Autowired
    private TransporterMasterRepository transporterMasterRepository;

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private CustomerMasterRepository customerMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Autowired
    private SiteMasterRepository siteMasterRepository;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public WeighmentPrintResponse getAllWeighmentTransactions(Integer ticketNo) {
        HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        }
        else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }

        // Check transaction exists with ticketNo or not
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);
        if(weighmentTransaction == null) {
            throw new ResourceNotFoundException("Ticket is not found with ticket no" + ticketNo);
        }

        WeighmentPrintResponse weighmentPrintResponse = new WeighmentPrintResponse();
        CompanyMaster companyMaster = companyMasterRepository.findById(userCompany)
                .orElseThrow(() -> new ResourceNotFoundException("Company is not found with id " + userCompany));
        weighmentPrintResponse.setCompanyName(companyMaster.getCompanyName());

        SiteMaster siteMaster = siteMasterRepository.findById(userSite)
                .orElseThrow(() -> new ResourceNotFoundException("Site is not found with id " + userSite));

        String companyAddress = siteMaster.getSiteName() +
                ", " +
                siteMaster.getSiteAddress();
        weighmentPrintResponse.setCompanyAddress(companyAddress);

        weighmentPrintResponse.setTicketNo(weighmentTransaction.getGateEntryTransaction().getTicketNo());

        String vehicleNo = vehicleMasterRepository.findVehicleNoById(weighmentTransaction.getGateEntryTransaction().getVehicleId());
        weighmentPrintResponse.setVehicleNo(vehicleNo);

        String materialName = materialMasterRepository.findMaterialNameByMaterialId(weighmentTransaction.getGateEntryTransaction().getMaterialId());
        weighmentPrintResponse.setMaterialName(materialName);

        String transporterName = transporterMasterRepository.findTransporterNameByTransporterId(weighmentTransaction.getGateEntryTransaction().getTransporterId());
        weighmentPrintResponse.setTransporterName(transporterName);

        if ("Inbound".equals(weighmentTransaction.getGateEntryTransaction().getTransactionType())) {
            String supplierName = supplierMasterRepository.findSupplierNameBySupplierId(weighmentTransaction.getGateEntryTransaction().getSupplierId());
            weighmentPrintResponse.setSupplierOrCustomerName(supplierName);
        }

        if ("Outbound".equals(weighmentTransaction.getGateEntryTransaction().getTransactionType())) {
            String customerName = customerMasterRepository.findCustomerMasterByCustomerId(weighmentTransaction.getGateEntryTransaction().getCustomerId());
            weighmentPrintResponse.setSupplierOrCustomerName(customerName);
        }

        weighmentPrintResponse.setChallanNo(weighmentTransaction.getGateEntryTransaction().getChallanNo());
        weighmentPrintResponse.setGrossWeight(weighmentTransaction.getGrossWeight());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        weighmentPrintResponse.setGrossWeight(weighmentTransaction.getGrossWeight());
        TransactionLog gwt = transactionLogRepository.findByTicketNoAndStatusCode(weighmentTransaction.getGateEntryTransaction().getTicketNo(), "GWT");
        if(gwt != null) {
            LocalDateTime gwtTimestamp = gwt.getTimestamp();
            String formattedGwtTimestamp = gwtTimestamp.format(formatter);
            weighmentPrintResponse.setGrossWeightDateTime(formattedGwtTimestamp);
        }

        weighmentPrintResponse.setTareWeight(weighmentTransaction.getTareWeight());
        TransactionLog twt = transactionLogRepository.findByTicketNoAndStatusCode(weighmentTransaction.getGateEntryTransaction().getTicketNo(), "TWT");
        if(twt != null) {
            LocalDateTime twtTimestamp = twt.getTimestamp();
            String formattedTwtTimestamp = twtTimestamp.format(formatter);
            weighmentPrintResponse.setTareWeightDateTime(formattedTwtTimestamp);
        }
            weighmentPrintResponse.setNetWeight(weighmentTransaction.getNetWeight());

        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));
        StringBuilder userName = new StringBuilder();
        userName.append(userMaster.getUserFirstName()).append(" ");
        if (userMaster.getUserMiddleName() != null){
            userName.append(userMaster.getUserMiddleName()).append(" ");
        }
        userName.append(userMaster.getUserLastName());
        weighmentPrintResponse.setOperatorName(String.valueOf(userName));
        return weighmentPrintResponse;
    }

    private boolean isValidTransaction(GateEntryTransactionResponse response, LocalDate startDate, LocalDate endDate) {
        LocalDate transactionDate = response.getTransactionDate();

        // Check if the transaction is within the specified date range
        boolean isWithinDateRange = !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);

        // Check if the transaction is valid based on the status code and transaction type
        boolean isValidTransaction = false;
        if (response.getTransactionType().equalsIgnoreCase("Inbound")) {
            isValidTransaction = transactionLogRepository.existsByTicketNoAndStatusCode(response.getTicketNo(), "TWT");
        } else if (response.getTransactionType().equalsIgnoreCase("Outbound")) {
            isValidTransaction = transactionLogRepository.existsByTicketNoAndStatusCode(response.getTicketNo(), "GWT");
        }

        // Return true only if the transaction is within the date range and is valid based on the status code
        return isWithinDateRange && isValidTransaction;
    }

    /**
     *
     * @param startDate The starting date for the report (format: YYYY-MM-DD), optional.
     *                  If not provided, end Date will considered as startDate.
     * @param endDate The ending date for the report (format: YYYY-MM-DD), optional.
                        If not provided, start Date will considered as endDate.
     * @return
     */
    public List<WeighbridgeReportResponse> generateWeighmentReport(LocalDate startDate, LocalDate endDate) {

        List<GateEntryTransactionResponse> gateEntryTransactionResponses = gateEntryTransactionService.getAllGateEntryTransactionForWeighmentReport(startDate,endDate);

        Map<String, Map<String, List<WeighbridgeReportResponseList>>> groupedReports = new HashMap<>();

        gateEntryTransactionResponses.stream()
                .forEach(response -> {
                    String materialName = response.getMaterial();
                    String supplierOrCustomer = response.getTransactionType().equalsIgnoreCase("Inbound") ?
                            response.getSupplier() : response.getCustomer();
                    String key = materialName + "-" + supplierOrCustomer;

                    WeighbridgeReportResponseList weighbridgeResponse2 = mapToWeighbridgeReportResponse(response);
                    groupedReports.computeIfAbsent(key, k -> new HashMap<>())
                            .computeIfAbsent(supplierOrCustomer, k -> new ArrayList<>())
                            .add(weighbridgeResponse2);
                });

        return groupedReports.entrySet().stream()
                .map(entry -> {
                    WeighbridgeReportResponse report = new WeighbridgeReportResponse();
                    String[] keys = entry.getKey().split("-");
                    report.setMaterialName(keys[0]);
                    report.setSupplierOrCustomer(keys[1]);
                    report.setWeighbridgeResponse2List(new ArrayList<>(entry.getValue().values()).get(0));
                    return report;
                })
                .collect(Collectors.toList());
    }

    /**
     * simply it'll return the WeighbridgeReportResponseList to called method so that report can be generated
     * @param gateEntryResponse
     * @return
     */
    private WeighbridgeReportResponseList mapToWeighbridgeReportResponse(GateEntryTransactionResponse gateEntryResponse) {
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(gateEntryResponse.getTicketNo());
        WeighbridgeReportResponseList weighbridgeReportResponseList = new WeighbridgeReportResponseList();
        weighbridgeReportResponseList.setTransactionDate(gateEntryResponse.getTransactionDate());
        weighbridgeReportResponseList.setVehicleNo(gateEntryResponse.getVehicleNo());
        weighbridgeReportResponseList.setTpNo(gateEntryResponse.getTpNo());
        weighbridgeReportResponseList.setChallanDate(gateEntryResponse.getChallanDate());

        if (weighmentTransaction != null) {
            double supplyConsignmentWeight = gateEntryResponse.getTpNetWeight();
            weighbridgeReportResponseList.setSupplyConsignmentWeight(supplyConsignmentWeight);
            /*
            SupplyConsignmentWeight is what it'll given at the time of gate entry, it means provided by supplier or Own Company Sales team
             */
            Double netWeight = (Double) weighmentTransaction.getNetWeight() != null ? weighmentTransaction.getNetWeight() : 0.0;
            weighbridgeReportResponseList.setWeighQuantity(netWeight);
            weighbridgeReportResponseList.setExcessQty(supplyConsignmentWeight - netWeight);
        } else {
            weighbridgeReportResponseList.setSupplyConsignmentWeight(0.0);
            weighbridgeReportResponseList.setWeighQuantity(0.0);
            weighbridgeReportResponseList.setExcessQty(0.0);
        }

        return weighbridgeReportResponseList;
    }
    @Override
    public List<Map<String, Object>> generateCustomizedReport(List<String> selectedFields,LocalDate startDate,LocalDate endDate) {
        HttpSession session = httpServletRequest.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }

        String userSite = session.getAttribute("userSite").toString();
        String userCompany = session.getAttribute("userCompany").toString();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        Root<GateEntryTransaction> gateEntryTransactionRoot = criteriaQuery.from(GateEntryTransaction.class);
        Root<WeighmentTransaction> weighmentTransactionRoot = criteriaQuery.from(WeighmentTransaction.class);

        // Build selection criteria based on selectedFields
        List<Selection<?>> selections = new ArrayList<>();
        Map<String, Expression<?>> fieldToExpressionMap = new HashMap<>();
        for (String field : selectedFields) {
            switch (field) {
                case "materialId":
                case "supplierId":
                case "customerId":
                case "vehicleId":
                case "transactionDate":
                case "challanNo":
                case "challanDate":
                case "supplyConsignmentWeight":
                case "ticketNo":
                    Selection<?> selection = gateEntryTransactionRoot.get(field);
                    selections.add(selection.alias(field));
                    fieldToExpressionMap.put(field, gateEntryTransactionRoot.get(field));
                    break;
                case "netWeight":
                    selections.add(weighmentTransactionRoot.get("netWeight").alias("netWeight"));
                    fieldToExpressionMap.put(field, weighmentTransactionRoot.get("netWeight"));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field name: " + field);
            }
        }
        criteriaQuery.multiselect(selections);

        // Add where clause for siteId and companyId
        Predicate siteCompanyPredicate = criteriaBuilder.and(
                criteriaBuilder.equal(gateEntryTransactionRoot.get("siteId"), userSite),
                criteriaBuilder.equal(gateEntryTransactionRoot.get("companyId"), userCompany),
                criteriaBuilder.equal(gateEntryTransactionRoot.get("ticketNo"), weighmentTransactionRoot.get("gateEntryTransaction").get("ticketNo"))
        );
        // Add date filtering predicates if startDate or endDate are provided
        if (startDate != null && endDate != null) {
            siteCompanyPredicate = criteriaBuilder.and(
                    siteCompanyPredicate,
                    criteriaBuilder.between(gateEntryTransactionRoot.get("transactionDate"), startDate, endDate)
            );
        } else if (startDate != null) {
            siteCompanyPredicate = criteriaBuilder.and(
                    siteCompanyPredicate,
                    criteriaBuilder.greaterThanOrEqualTo(gateEntryTransactionRoot.get("transactionDate"), startDate)
            );
        } else if (endDate != null) {
            siteCompanyPredicate = criteriaBuilder.and(
                    siteCompanyPredicate,
                    criteriaBuilder.lessThanOrEqualTo(gateEntryTransactionRoot.get("transactionDate"), endDate)
            );
        }
        criteriaQuery.where(siteCompanyPredicate);

        // Order by transactionDate descending
        criteriaQuery.orderBy(criteriaBuilder.desc(gateEntryTransactionRoot.get("transactionDate")));

        List<Tuple> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        System.out.println("result list tuple "+resultList);
        List<Map<String, Object>> mappedResultList = new ArrayList<>();
        for (Tuple tuple : resultList) {
            Map<String, Object> mappedResult = new HashMap<>();
            for (String field : selectedFields) {
                Object value = tuple.get(fieldToExpressionMap.get(field));
                if ("materialId".equals(field) && value != null) {
                    long materialId = (long) value;
                    value = Optional.ofNullable(materialMasterRepository.findMaterialNameByMaterialId(materialId))
                            .orElse("Unknown Material");
                } else if ("supplierId".equals(field) && value != null) {
                    long supplierId = (long) value;
                    value = Optional.ofNullable(supplierMasterRepository.findSupplierNameBySupplierId(supplierId))
                            .orElse("");
                } else if ("customerId".equals(field) && value != null) {
                    long customerId = (long) value;
                    value = Optional.ofNullable(customerMasterRepository.findCustomerNameByCustomerId(customerId))
                            .orElse("");
                } else if ("vehicleId".equals(field) && value != null) {
                    long vehicleId = (long) value;
                    value = Optional.ofNullable(vehicleMasterRepository.findVehicleNoById(vehicleId))
                            .orElse("");
                }
                mappedResult.put(field, value);
            }
            mappedResultList.add(mappedResult);
        }

        return mappedResultList;
    }

}
