package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.admin.entities.CompanyMaster;
import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.entities.UserMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.gateuser.entities.TransactionLog;
import com.weighbridge.gateuser.payloads.GateEntryTransactionResponse;
import com.weighbridge.gateuser.repositories.TransactionLogRepository;
import com.weighbridge.gateuser.services.GateEntryTransactionService;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgeReportResponseList;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentPrintResponse;
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

    private final Double ch_SumQtyC = 0.0;
    private final Double weight_SumQtyC = 0.0;
    private final Double shtExcess_SumQtyc = 0.0;


    @Override
    public WeighmentPrintResponse getAllWeighmentTransactions(Integer ticketNo,String userId) {
     /*   HttpSession session = httpServletRequest.getSession();
        String userId;
        String userCompany;
        String userSite;
        if (session != null && session.getAttribute("userId") != null) {
            userId = session.getAttribute("userId").toString();
            userSite = session.getAttribute("userSite").toString();
            userCompany = session.getAttribute("userCompany").toString();
        } else {
            throw new SessionExpiredException("Session Expired, Login again !");
        }*/

        // Check transaction exists with ticketNo or not
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(ticketNo);
        if (weighmentTransaction == null) {
            throw new ResourceNotFoundException("Ticket is not found with ticket no" + ticketNo);
        }

        WeighmentPrintResponse weighmentPrintResponse = new WeighmentPrintResponse();
        CompanyMaster companyMaster = companyMasterRepository.findById(weighmentTransaction.getGateEntryTransaction().getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company is not found with id " +weighmentTransaction.getGateEntryTransaction().getCompanyId()));
        weighmentPrintResponse.setCompanyName(companyMaster.getCompanyName());

        SiteMaster siteMaster = siteMasterRepository.findById(weighmentTransaction.getGateEntryTransaction().getSiteId())
                .orElseThrow(() -> new ResourceNotFoundException("Site is not found with id " + weighmentTransaction.getGateEntryTransaction().getSiteId()));

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
            weighmentPrintResponse.setTareWeight(weighmentTransaction.getTareWeight());
            weighmentPrintResponse.setGrossWeight(weighmentTransaction.getTemporaryWeight());
        }

        if ("Outbound".equals(weighmentTransaction.getGateEntryTransaction().getTransactionType())) {
            String customerName = customerMasterRepository.findCustomerNameByCustomerId(weighmentTransaction.getGateEntryTransaction().getCustomerId());
            weighmentPrintResponse.setSupplierOrCustomerName(customerName);
            weighmentPrintResponse.setTareWeight(weighmentTransaction.getTemporaryWeight());
            weighmentPrintResponse.setGrossWeight(weighmentTransaction.getGrossWeight());
        }
        System.out.println(weighmentPrintResponse);

        weighmentPrintResponse.setChallanNo(weighmentTransaction.getGateEntryTransaction().getChallanNo());
        //  weighmentPrintResponse.setGrossWeight(weighmentTransaction.getGrossWeight());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //    weighmentPrintResponse.setGrossWeight(weighmentTransaction.getGrossWeight());
        TransactionLog gwt = transactionLogRepository.findByTicketNoAndStatusCode(weighmentTransaction.getGateEntryTransaction().getTicketNo(), "GWT");
        if (gwt != null) {
            LocalDateTime gwtTimestamp = gwt.getTimestamp();
            String formattedGwtTimestamp = gwtTimestamp.format(formatter);
            weighmentPrintResponse.setGrossWeightDateTime(formattedGwtTimestamp);
        }
        // weighmentPrintResponse.setTareWeight(weighmentTransaction.getTareWeight());
        TransactionLog twt = transactionLogRepository.findByTicketNoAndStatusCode(weighmentTransaction.getGateEntryTransaction().getTicketNo(), "TWT");
        if (twt != null) {
            LocalDateTime twtTimestamp = twt.getTimestamp();
            String formattedTwtTimestamp = twtTimestamp.format(formatter);
            weighmentPrintResponse.setTareWeightDateTime(formattedTwtTimestamp);
        }
        weighmentPrintResponse.setNetWeight(weighmentTransaction.getNetWeight());

        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "user id", userId));
        StringBuilder userName = new StringBuilder();
        userName.append(userMaster.getUserFirstName()).append(" ");
        if (userMaster.getUserMiddleName() != null) {
            userName.append(userMaster.getUserMiddleName()).append(" ");
        }
        userName.append(userMaster.getUserLastName());
        weighmentPrintResponse.setOperatorName(String.valueOf(userName));
        System.out.println(weighmentPrintResponse);
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
     * @param startDate The starting date for the report (format: YYYY-MM-DD), optional.
     *                  If not provided, end Date will considered as startDate.
     * @param endDate   The ending date for the report (format: YYYY-MM-DD), optional.
     *                  If not provided, start Date will considered as endDate.
     * @return
     */
    public List<WeighbridgeReportResponse> generateWeighmentReport(LocalDate startDate, LocalDate endDate,String companyName, String siteName,String userId) {
        List<GateEntryTransactionResponse> gateEntryTransactionResponses = gateEntryTransactionService.getAllGateEntryTransactionForWeighmentReport(startDate, endDate,companyName,siteName,userId);

        System.out.println("  asdf"+gateEntryTransactionResponses);
//        Map<String, Map<String, List<WeighbridgeReportResponseList>>> groupedReports = new HashMap<>();

        Map<String, List<WeighbridgeReportResponseList>> groupedReports = new HashMap<>();

        gateEntryTransactionResponses.stream()
                .forEach(response -> {
                    // Determine the key for grouping
                    String materialName = response.getMaterial();
                    String supplierOrCustomer = response.getTransactionType().equalsIgnoreCase("Inbound") ?
                            response.getSupplier() : response.getCustomer();
                    String key = materialName + "-" + (supplierOrCustomer != null ? supplierOrCustomer : "Unknown");

                    // Map to weighbridge report response
                    WeighbridgeReportResponseList weighbridgeResponse = mapToWeighbridgeReportResponse(response);

                    // Add to group only if weighbridgeResponse is not null
                    if (weighbridgeResponse != null) {
                        groupedReports.computeIfAbsent(key, k -> new ArrayList<>()).add(weighbridgeResponse);
                    }
                });

        // Transform the map to a list of WeighbridgeReportResponse
        List<WeighbridgeReportResponse> reportList = groupedReports.entrySet().stream()
                .map(entry -> {
                    WeighbridgeReportResponse report = new WeighbridgeReportResponse();
                    String[] keys = entry.getKey().split("-", 2);

                    report.setMaterialName(keys[0]);
                    System.out.println("Material Name: " + keys[0]);

                    report.setSupplierOrCustomer(keys.length > 1 ? keys[1] : "Unknown");
                    System.out.println("Supplier or Customer: " + keys[1]);

                    // Get the list of weighbridge responses
                    List<WeighbridgeReportResponseList> weighbridgeResponseList = entry.getValue();
                    System.out.println("Weighbridge Responses: " + weighbridgeResponseList);

                    // Calculate sums with null safety checks
                    double ch_SumQty = weighbridgeResponseList.stream().filter(response -> response.getSupplyConsignmentWeight() != null).mapToDouble(WeighbridgeReportResponseList::getSupplyConsignmentWeight).sum();

                    double weight_SumQty = weighbridgeResponseList.stream().filter(response -> response.getWeighQuantity() != null).mapToDouble(WeighbridgeReportResponseList::getWeighQuantity).sum();

                    double shtExcess_SumQty = weighbridgeResponseList.stream().filter(response -> response.getExcessQty() != null).mapToDouble(WeighbridgeReportResponseList::getExcessQty).sum();

                    // Set the list and sums in the report
                    report.setWeighbridgeResponse2List(weighbridgeResponseList);
                    report.setCh_SumQty(ch_SumQty);
                    report.setWeight_SumQty(weight_SumQty);
                    report.setShtExcess_SumQty(shtExcess_SumQty);

                    return report;
                })
                .collect(Collectors.toList());

        return reportList;

    }

    /**
     * simply it'll return the WeighbridgeReportResponseList to called method so that report can be generated
     * Utility method to map GateEntryTransactionResponse to WeighbridgeReportResponseList
     * @param gateEntryResponse
     * @return WeighbridgeReportResponseList or null if weighmentTransaction is not found
     */
    private WeighbridgeReportResponseList mapToWeighbridgeReportResponse(GateEntryTransactionResponse gateEntryResponse) {
        WeighmentTransaction weighmentTransaction = weighmentTransactionRepository.findByGateEntryTransactionTicketNo(gateEntryResponse.getTicketNo());
        // Define the formatter for the desired date format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        if (weighmentTransaction == null) {
            return null;
        }
        WeighbridgeReportResponseList weighbridgeReportResponseList = new WeighbridgeReportResponseList();
        String formatTransactionDate = gateEntryResponse.getTransactionDate().format(dateFormatter);
        weighbridgeReportResponseList.setTransactionDate(formatTransactionDate);
        weighbridgeReportResponseList.setVehicleNo(gateEntryResponse.getVehicleNo());
        weighbridgeReportResponseList.setTpNo(gateEntryResponse.getTpNo());


        // Get the LocalDate from gateEntryResponse
        LocalDate challanDate = gateEntryResponse.getChallanDate();

        if (challanDate != null) {
            // Format the LocalDate to a string with the desired format
            String formattedDate = challanDate.format(dateFormatter);
            weighbridgeReportResponseList.setFormattedChallanDate(formattedDate);
        }
        weighbridgeReportResponseList.setChallanDate(challanDate);
        weighbridgeReportResponseList.setTicketNo(gateEntryResponse.getTicketNo());

        if (weighmentTransaction != null) {

            // Calculate weights with null checks
            Double supplyConsignmentWeight = gateEntryResponse.getTpNetWeight();
            if (supplyConsignmentWeight != null) {

                weighbridgeReportResponseList.setSupplyConsignmentWeight(supplyConsignmentWeight);
            } else {
                supplyConsignmentWeight = 0.0;
            }

            Double netWeight = (Double) weighmentTransaction.getNetWeight() != null ? weighmentTransaction.getNetWeight() : 0.0;
            weighbridgeReportResponseList.setWeighQuantity(netWeight);
            double excessQty = supplyConsignmentWeight - netWeight;
            weighbridgeReportResponseList.setExcessQty(excessQty);


        }
        return weighbridgeReportResponseList;
    }

    @Override
    public List<Map<String, Object>> generateCustomizedReport(List<String> selectedFields, LocalDate
            startDate, LocalDate endDate,String userId) {
       /* HttpSession session = httpServletRequest.getSession();
        if (session == null || session.getAttribute("userId") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session Expired, Login again !");
        }
*/
        UserMaster userMaster = userMasterRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with " + userId));
        String userSite = userMaster.getSite().getSiteId();
        String userCompany =userMaster.getCompany().getCompanyId();

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
        Predicate siteCompanyPredicate = criteriaBuilder.and(criteriaBuilder.equal(gateEntryTransactionRoot.get("siteId"), userSite), criteriaBuilder.equal(gateEntryTransactionRoot.get("companyId"), userCompany), criteriaBuilder.equal(gateEntryTransactionRoot.get("ticketNo"), weighmentTransactionRoot.get("gateEntryTransaction").get("ticketNo")));
        // Add date filtering predicates if startDate or endDate are provided
        if (startDate != null && endDate != null) {
            siteCompanyPredicate = criteriaBuilder.and(siteCompanyPredicate, criteriaBuilder.between(gateEntryTransactionRoot.get("transactionDate"), startDate, endDate));
        } else if (startDate != null) {
            siteCompanyPredicate = criteriaBuilder.and(siteCompanyPredicate, criteriaBuilder.greaterThanOrEqualTo(gateEntryTransactionRoot.get("transactionDate"), startDate));
        } else if (endDate != null) {
            siteCompanyPredicate = criteriaBuilder.and(siteCompanyPredicate, criteriaBuilder.lessThanOrEqualTo(gateEntryTransactionRoot.get("transactionDate"), endDate));
        }
        criteriaQuery.where(siteCompanyPredicate);

        // Order by transactionDate descending
        criteriaQuery.orderBy(criteriaBuilder.desc(gateEntryTransactionRoot.get("transactionDate")));

        List<Tuple> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        System.out.println("result list tuple " + resultList);
        List<Map<String, Object>> mappedResultList = new ArrayList<>();
        for (Tuple tuple : resultList) {
            Map<String, Object> mappedResult = new HashMap<>();
            for (String field : selectedFields) {
                Object value = tuple.get(fieldToExpressionMap.get(field));
                if ("materialId".equals(field) && value != null) {
                    long materialId = (long) value;
                    value = Optional.ofNullable(materialMasterRepository.findMaterialNameByMaterialId(materialId)).orElse("Unknown Material");
                } else if ("supplierId".equals(field) && value != null) {
                    long supplierId = (long) value;
                    value = Optional.ofNullable(supplierMasterRepository.findSupplierNameBySupplierId(supplierId)).orElse("");
                } else if ("customerId".equals(field) && value != null) {
                    long customerId = (long) value;
                    value = Optional.ofNullable(customerMasterRepository.findCustomerNameByCustomerId(customerId)).orElse("");
                } else if ("vehicleId".equals(field) && value != null) {
                    long vehicleId = (long) value;
                    value = Optional.ofNullable(vehicleMasterRepository.findVehicleNoById(vehicleId)).orElse("");
                }
                mappedResult.put(field, value);
            }
            mappedResultList.add(mappedResult);
        }
        return mappedResultList;
    }
}
