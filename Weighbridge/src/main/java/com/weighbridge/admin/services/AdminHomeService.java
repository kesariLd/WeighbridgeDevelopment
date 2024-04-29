package com.weighbridge.admin.services;

public interface AdminHomeService {

    long findNoOfActiveUsers();

    long findNoOfInActiveUsers();

    long findNoOfRegisteredVehicle();
}
