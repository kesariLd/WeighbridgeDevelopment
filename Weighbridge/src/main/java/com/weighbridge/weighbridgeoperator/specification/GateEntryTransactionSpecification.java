package com.weighbridge.weighbridgeoperator.specification;

import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.gateuser.entities.GateEntryTransaction;
import com.weighbridge.weighbridgeoperator.dto.WeighbridgeOperatorSearchCriteria;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import com.weighbridge.weighbridgeoperator.repositories.WeighmentTransactionRepository;
import jakarta.persistence.criteria.*;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class GateEntryTransactionSpecification implements Specification<GateEntryTransaction> {

    private final WeighbridgeOperatorSearchCriteria criteria;


    private final VehicleMasterRepository vehicleMasterRepository;
    private final MaterialMasterRepository materialMasterRepository;


    private final TransporterMasterRepository transporterMasterRepository;
    private final ProductMasterRepository productMasterRepository;
    private final SupplierMasterRepository supplierMasterRepository;
    private final CustomerMasterRepository customerMasterRepository;

    private final WeighmentTransactionRepository weighmentTransactionRepository;



    public GateEntryTransactionSpecification(WeighbridgeOperatorSearchCriteria criteria, VehicleMasterRepository  vehicleMasterRepository, MaterialMasterRepository materialMasterRepository, TransporterMasterRepository transporterMasterRepository, ProductMasterRepository productMasterRepository, SupplierMasterRepository supplierMasterRepository, CustomerMasterRepository customerMasterRepository, WeighmentTransactionRepository weighmentTransactionRepository) {
        this.criteria = criteria;
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.materialMasterRepository = materialMasterRepository;
        this.transporterMasterRepository = transporterMasterRepository;
        this.productMasterRepository = productMasterRepository;
        this.supplierMasterRepository = supplierMasterRepository;
        this.customerMasterRepository = customerMasterRepository;
        this.weighmentTransactionRepository = weighmentTransactionRepository;
    }

    @Override
    public Predicate toPredicate(Root<GateEntryTransaction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (criteria.getTicketNo() != null) {
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("ticketNo"), criteria.getTicketNo()),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }
        if (criteria.getTransactionType() != null) {
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("transactionType"), criteria.getTransactionType()),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }
        if (criteria.getTransactionDate() != null) {
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("transactionDate"), criteria.getTransactionDate()),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }
        if (criteria.getVehicleNo() != null) {
            VehicleMaster byVehicleNo = vehicleMasterRepository.findByVehicleNo(criteria.getVehicleNo());
            if (byVehicleNo != null) {
                Predicate combinedPredicate = builder.and(
                        builder.equal(root.get("vehicleId"), byVehicleNo.getId()),
                        builder.equal(root.get("siteId"), criteria.getSiteId()),
                        builder.equal(root.get("companyId"), criteria.getCompanyId())
                );
                predicates.add(combinedPredicate);
            } else {
                throw new ResourceNotFoundException("vehicle not found with vehicleNo " + criteria.getVehicleNo());
            }
        }
        if (criteria.getTransporterName() != null) {
            long transporterIdByTransporterName = transporterMasterRepository.findTransporterIdByTransporterName(criteria.getTransporterName());
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("transporterId"), transporterIdByTransporterName),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }

        if (criteria.getMaterialName() != null) {
            long byMaterialIdByMaterialName = materialMasterRepository.findByMaterialIdByMaterialName(criteria.getMaterialName());
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("materialId"), byMaterialIdByMaterialName),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }
        if (criteria.getProductName() != null) {
            long productIdByProductName = productMasterRepository.findProductIdByProductName(criteria.getProductName());
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("materialId"), productIdByProductName),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }
        if (criteria.getSupplierName() != null) {
            List<Long> listSupplierIdBySupplierName = supplierMasterRepository.findListSupplierIdBySupplierName(criteria.getSupplierName());
            if (listSupplierIdBySupplierName.isEmpty()) {
                throw new ResourceNotFoundException("Supplier Not found with supplierName " + criteria.getSupplierName());
            } else {
                Predicate supplierPredicate = root.get("supplierId").in(listSupplierIdBySupplierName);
                Predicate sitePredicate = builder.equal(root.get("siteId"), criteria.getSiteId());
                Predicate companyPredicate = builder.equal(root.get("companyId"), criteria.getCompanyId());
                Predicate combinedPredicate = builder.and(supplierPredicate, sitePredicate, companyPredicate);
                predicates.add(combinedPredicate);
            }
        }
        if (criteria.getCustomerName() != null) {
            List<Long> listCustomerIdByCustomerName = customerMasterRepository.findListCustomerIdbyCustomerName(criteria.getCustomerName());
            System.out.println(listCustomerIdByCustomerName);
            if (listCustomerIdByCustomerName.isEmpty()) {
                throw new ResourceNotFoundException("customer not found with customerName " + criteria.getCustomerName());
            } else {
                Predicate supplierPredicate = root.get("customerId").in(listCustomerIdByCustomerName);
                Predicate sitePredicate = builder.equal(root.get("siteId"), criteria.getSiteId());
                Predicate companyPredicate = builder.equal(root.get("companyId"), criteria.getCompanyId());
                Predicate combinedPredicate = builder.and(supplierPredicate, sitePredicate, companyPredicate);
                predicates.add(combinedPredicate);
            }

        }

        if (Boolean.TRUE.equals(criteria.getToday())) {
            LocalDate today = LocalDate.now();
            Predicate combinedPredicate = builder.and(
                    builder.equal(root.get("transactionDate"), today),
                    builder.equal(root.get("siteId"), criteria.getSiteId()),
                    builder.equal(root.get("companyId"), criteria.getCompanyId())
            );
            predicates.add(combinedPredicate);
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    public Specification<GateEntryTransaction> netWeightZero() {
        return (root, query, criteriaBuilder) -> {
            // Subquery to fetch GateEntryTransaction IDs that are referenced in WeighmentTransaction with netWeight 0.0
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<WeighmentTransaction> weighmentTransactionRoot = subquery.from(WeighmentTransaction.class);
            subquery.select(weighmentTransactionRoot.get("gateEntryTransaction").get("id"));
            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(weighmentTransactionRoot.get("gateEntryTransaction").get("siteId"), criteria.getSiteId()),
                            criteriaBuilder.equal(weighmentTransactionRoot.get("gateEntryTransaction").get("companyId"), criteria.getCompanyId()),
                            criteriaBuilder.equal(weighmentTransactionRoot.get("netWeight"), 0.0)
                    )
            );

            // Main query predicates
            Predicate siteIdPredicate = criteriaBuilder.equal(root.get("siteId"), criteria.getSiteId());
            Predicate companyIdPredicate = criteriaBuilder.equal(root.get("companyId"), criteria.getCompanyId());
            Predicate notInWeighmentTransactionPredicate = criteriaBuilder.not(root.get("id").in(subquery));
            Predicate inWeighmentTransactionWithZeroNetWeightPredicate = criteriaBuilder.in(root.get("id")).value(subquery);

            // Combine predicates using OR to include transactions not in weighmentTransaction or with zero net weight
            return criteriaBuilder.and(
                    siteIdPredicate,
                    companyIdPredicate,
                    criteriaBuilder.or(notInWeighmentTransactionPredicate, inWeighmentTransactionWithZeroNetWeightPredicate)
            );
        };
    }

}