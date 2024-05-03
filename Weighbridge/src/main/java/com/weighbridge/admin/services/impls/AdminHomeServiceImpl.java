package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.repsitories.TransporterMasterRepository;
import com.weighbridge.admin.repsitories.UserMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.admin.services.AdminHomeService;
import org.springframework.stereotype.Service;

@Service
public class AdminHomeServiceImpl implements AdminHomeService {

    private final VehicleMasterRepository vehicleMasterRepository;
    private final UserMasterRepository userMasterRepository;
    private final TransporterMasterRepository transporterMasterRepository;
    private final CompanyMasterRepository companyMasterRepository;
    private final SupplierMasterRepository supplierMasterRepository;
    private final CustomerMasterRepository customerMasterRepository;

    public AdminHomeServiceImpl(VehicleMasterRepository vehicleMasterRepository,
                                UserMasterRepository userMasterRepository,
                                TransporterMasterRepository transporterMasterRepository,
                                CompanyMasterRepository companyMasterRepository,
                                SupplierMasterRepository supplierMasterRepository,
                                CustomerMasterRepository customerMasterRepository) {
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.userMasterRepository = userMasterRepository;
        this.transporterMasterRepository = transporterMasterRepository;
        this.companyMasterRepository = companyMasterRepository;
        this.supplierMasterRepository = supplierMasterRepository;
        this.customerMasterRepository = customerMasterRepository;
    }

    @Override
    public long findNoOfActiveUsers() {
        return userMasterRepository.countByUserStatus("ACTIVE");
    }

    @Override
    public long findNoOfInActiveUsers() {
        return userMasterRepository.countByUserStatus("INACTIVE");
    }

    @Override
    public long findNoOfRegisteredVehicle() {
        return vehicleMasterRepository.count();
    }

    @Override
    public long findNoOfAllUsers() {
        return userMasterRepository.count();
    }

    @Override
    public long findNoOfRegisteredCustomers() {
        return customerMasterRepository.count();
    }

    @Override
    public long findNoOfRegisteredSuppliers() {
        return supplierMasterRepository.count();
    }

    @Override
    public long findNoOfRegisteredTransporters() {
        return transporterMasterRepository.count();
    }

    @Override
    public long findNoOfRegisteredCompanies() {
        return companyMasterRepository.countByCompanyNameNot();
    }
}
