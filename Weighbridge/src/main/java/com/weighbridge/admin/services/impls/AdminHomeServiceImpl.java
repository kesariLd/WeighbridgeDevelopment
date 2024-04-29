package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.repsitories.UserMasterRepository;
import com.weighbridge.admin.repsitories.VehicleMasterRepository;
import com.weighbridge.admin.services.AdminHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminHomeServiceImpl implements AdminHomeService {

    @Autowired
    private VehicleMasterRepository vehicleMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;
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
}
