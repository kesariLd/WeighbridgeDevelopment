package com.weighbridge.admin.services;

public interface AdminHomeService {

    long findNoOfActiveUsers();

    long findNoOfInActiveUsers();

    long findNoOfRegisteredVehicle();

    long findNoOfAllUsers();

    long findNoOfRegisteredCustomers();

    long findNoOfRegisteredSuppliers();

    long findNoOfRegisteredTransporters();

    long findNoOfRegisteredCompanies();
}
