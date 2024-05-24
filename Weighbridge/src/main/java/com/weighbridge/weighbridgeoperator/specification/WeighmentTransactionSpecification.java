package com.weighbridge.weighbridgeoperator.specification;

import com.weighbridge.admin.entities.VehicleMaster;
import com.weighbridge.admin.repsitories.*;
import com.weighbridge.weighbridgeoperator.entities.WeighmentTransaction;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import com.weighbridge.weighbridgeoperator.dto.WeighbridgeOperatorSearchCriteria;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



public class WeighmentTransactionSpecification implements Specification<WeighmentTransaction> {

    private final WeighbridgeOperatorSearchCriteria criteria;


    private final VehicleMasterRepository  vehicleMasterRepository;
    private final MaterialMasterRepository materialMasterRepository;


    private final TransporterMasterRepository transporterMasterRepository;
    private final ProductMasterRepository productMasterRepository;
    private final SupplierMasterRepository supplierMasterRepository;
    private final CustomerMasterRepository customerMasterRepository;



    public WeighmentTransactionSpecification(WeighbridgeOperatorSearchCriteria criteria, VehicleMasterRepository  vehicleMasterRepository, MaterialMasterRepository materialMasterRepository, TransporterMasterRepository transporterMasterRepository, ProductMasterRepository productMasterRepository, SupplierMasterRepository supplierMasterRepository, CustomerMasterRepository customerMasterRepository) {
        this.criteria = criteria;
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.materialMasterRepository = materialMasterRepository;
        this.transporterMasterRepository = transporterMasterRepository;
        this.productMasterRepository = productMasterRepository;
        this.supplierMasterRepository = supplierMasterRepository;
        this.customerMasterRepository = customerMasterRepository;
    }

    @Override
    public Predicate toPredicate(Root<WeighmentTransaction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if(criteria.getTicketNo()!=null){
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("ticketNo"),criteria.getTicketNo()));
        }
        if (criteria.getTransactionType() != null) {
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("transactionType"), criteria.getTransactionType()));
        }
        if (criteria.getTransactionDate() != null) {
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("transactionDate"), criteria.getTransactionDate()));
        }
        if (criteria.getVehicleNo() != null) {
                VehicleMaster byVehicleNo = vehicleMasterRepository.findByVehicleNo(criteria.getVehicleNo());
                if (byVehicleNo != null) {
                    predicates.add(builder.equal(root.get("gateEntryTransaction").get("vehicleId"), byVehicleNo.getId()));
                }
               else {
                   throw new RuntimeException("vehicle not found with vehicleNo "+criteria.getVehicleNo());
                }
        }
        if (criteria.getTransporterName() != null) {
            long transporterIdByTransporterName = transporterMasterRepository.findTransporterIdByTransporterName(criteria.getTransporterName());
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("transporterId"), transporterIdByTransporterName));
        }
        if (criteria.getMaterialName() != null) {
            long byMaterialIdByMaterialName = materialMasterRepository.findByMaterialIdByMaterialName(criteria.getMaterialName());
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("materialId"), byMaterialIdByMaterialName));
        }
        if (criteria.getProductName() != null) {
            long productIdByProductName = productMasterRepository.findProductIdByProductName(criteria.getProductName());
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("productId"), productIdByProductName));
        }
        if(criteria.getSupplierName()!=null){
            List<Long> listSupplierIdBySupplierName = supplierMasterRepository.findListSupplierIdBySupplierName(criteria.getSupplierName());
            System.out.println(listSupplierIdBySupplierName);
            if(listSupplierIdBySupplierName.isEmpty()){
                throw new RuntimeException("Supplier Not found with supplierName "+criteria.getSupplierName());
            }
            else {
                Predicate in = root.get("gateEntryTransaction").get("supplierId").in(listSupplierIdBySupplierName);
                predicates.add(in);
            }
        }
        if(criteria.getCustomerName()!=null){
            List<Long> listCustomerIdByCustomerName = customerMasterRepository.findListCustomerIdByCustomerName(criteria.getCustomerName());
            System.out.println(listCustomerIdByCustomerName);
            if(listCustomerIdByCustomerName.isEmpty()){
                throw  new RuntimeException("customer not found with customerName "+criteria.getCustomerName());
            }
            else {
                Predicate in = root.get("gateEntryTransaction").get("supplierId").in(listCustomerIdByCustomerName);
                predicates.add(in);
            }
        }
        if (Boolean.TRUE.equals(criteria.getToday())) {
            LocalDate today = LocalDate.now();
            predicates.add(builder.equal(root.get("gateEntryTransaction").get("transactionDate"), today));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}