package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.services.CustomerMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerMasterServiceImpl implements CustomerMasterService {
    private final CustomerMasterRepository customerMasterRepository;
    private final HttpServletRequest httpServletRequest;

    public CustomerMasterServiceImpl(CustomerMasterRepository customerMasterRepository, HttpServletRequest httpServletRequest) {
        this.customerMasterRepository = customerMasterRepository;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public String createSupplier(CustomerMasterDto customerMasterDto) {
        boolean exists = customerMasterRepository.existsByCustomerContactNoOrCustomerEmail(
                customerMasterDto.getCustomerContactNo(),
                customerMasterDto.getCustomerEmail());
        if (exists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Id or Contact No is already taken");
        }

        CustomerMaster customerMaster = new CustomerMaster();
        customerMaster.setCustomerName(customerMasterDto.getCustomerName());
        customerMaster.setCustomerEmail(customerMasterDto.getCustomerEmail());
        customerMaster.setCustomerContactNo(customerMasterDto.getCustomerContactNo());
        customerMaster.setCustomerAddressLine1(customerMasterDto.getCustomerAddressLine1());
        customerMaster.setCustomerAddressLine2(customerMasterDto.getCustomerAddressLine2());
        customerMaster.setCountry(customerMasterDto.getCountry());
        customerMaster.setState(customerMasterDto.getState());
        customerMaster.setCity(customerMasterDto.getCity());
        customerMaster.setZip(customerMasterDto.getZip());

        HttpSession session = httpServletRequest.getSession();
        String userId = String.valueOf(session.getAttribute("userId"));
        LocalDateTime currentTime = LocalDateTime.now();

        customerMaster.setCustomerCreatedBy(userId);
        customerMaster.setCustomerCreatedDate(currentTime);
        customerMaster.setCustomerModifiedBy(userId);
        customerMaster.setCustomerModifiedDate(currentTime);

        try {
            customerMasterRepository.save(customerMaster);
            return "Customer added successfully";
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred during creation");
        }
    }

    @Override
    public List<CustomerMasterDto> getAllCustomers() {
        List<CustomerMaster> customerMasterList = customerMasterRepository.findAll();

        List<CustomerMasterDto> customerMasterDtoList = customerMasterList.stream().map(customerMaster -> {
            CustomerMasterDto customerMasterDto = new CustomerMasterDto();
            customerMasterDto.setCustomerId(customerMaster.getCustomerId());
            customerMasterDto.setCustomerName(customerMaster.getCustomerName());
            customerMasterDto.setCustomerEmail(customerMaster.getCustomerEmail());
            customerMasterDto.setCustomerContactNo(customerMaster.getCustomerContactNo());
            customerMasterDto.setCustomerAddressLine1(customerMaster.getCustomerAddressLine1());
            customerMasterDto.setCustomerAddressLine2(customerMaster.getCustomerAddressLine2());
            customerMasterDto.setCountry(customerMaster.getCountry());
            customerMasterDto.setState(customerMaster.getState());
            customerMasterDto.setCity(customerMaster.getCity());
            customerMasterDto.setZip(customerMaster.getZip());
            return customerMasterDto;
        }).collect(Collectors.toList());
        return customerMasterDtoList;
    }

    @Override
    public List<String> getAllCustomerNames() {
        List<CustomerMaster> customerMasterList = customerMasterRepository.findAll();
        List<String> allCustomerNames = new ArrayList<>();
        customerMasterList.forEach(customerMaster -> {
            String customerName = customerMaster.getCustomerName();
            allCustomerNames.add(customerName);
        });
        return allCustomerNames;
    }

    @Override
    public List<String> getAddressOfCustomer(String name) {
        List<String> getAddress = customerMasterRepository.findCustomerAddressByCustomerName(name);
        return getAddress;
    }
}
