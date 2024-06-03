package com.weighbridge.gateuser.payloads;

import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.print.ServiceUI;
import java.time.LocalDate;
import java.util.List;

@Component
public class GateEntryTransactionSpecification {
    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;
    @Autowired
    private CustomerMasterRepository customerMasterRepository;
    @Autowired
    private SupplierMasterRepository supplierMasterRepository;
    public Specification<GateEntryTransaction> getTransactions(Integer ticketNo, String vehicleNo, LocalDate date,String supplierName,String transactionType,String vehicleStatus) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (ticketNo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("ticketNo"), ticketNo));
            }
            if(StringUtils.hasText(transactionType)){
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("transactionType"),transactionType));
            }

            if (StringUtils.hasText(vehicleNo)) {
                Long vehicleIdByVehicleNo = vehicleMasterRepository.findVehicleIdByVehicleNo(vehicleNo);
                if (vehicleIdByVehicleNo != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("vehicleId"), vehicleIdByVehicleNo));
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get("vehicleId")));
                }
            }

            if (date != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("transactionDate"), date));
            }
            if (vehicleStatus.equalsIgnoreCase("completed")) {// for complete dashboard
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNotNull(root.get("vehicleOut")));
            }
            if (vehicleStatus.equalsIgnoreCase("ongoing")) { //for ongoing or queue dashboard
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get("vehicleOut")));
            }

            if (StringUtils.hasText(supplierName)) {
                List<Long> supplierIds = supplierMasterRepository.findListSupplierIdBySupplierName(supplierName);
                List<Long> customerIds = customerMasterRepository.findListCustomerIdbyCustomerName(supplierName);
                if (!supplierIds.isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, root.get("supplierId").in(supplierIds));
                } else if (!customerIds.isEmpty()) {
                    predicate = criteriaBuilder.and(predicate, root.get("customerId").in(customerIds));
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get("supplierId")));
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(root.get("customerId")));
                }
            }

            return predicate;
        };
    }
    public Specification<GateEntryTransaction> filterBySiteAndCompany(String siteId, String companyId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("siteId"), siteId),
                criteriaBuilder.equal(root.get("companyId"), companyId)
        );
    }

}