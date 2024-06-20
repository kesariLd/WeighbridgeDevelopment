package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.CustomerMasterDto;
import com.weighbridge.admin.entities.CustomerMaster;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.payloads.CustomerRequest;
import com.weighbridge.admin.repsitories.CustomerMasterRepository;
import com.weighbridge.admin.services.CustomerMasterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.hibernate.sql.Update;
import org.modelmapper.ModelMapper;
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
    public String createCustomer(CustomerMasterDto customerMasterDto,String userId) {
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

        /*HttpSession session = httpServletRequest.getSession();
        String userId = session.getAttribute("userId").toString();*/
        LocalDateTime currentDateTime = LocalDateTime.now();

        customerMaster.setCustomerCreatedBy(userId);
        customerMaster.setCustomerCreatedDate(currentDateTime);
        customerMaster.setCustomerModifiedBy(userId);
        customerMaster.setCustomerModifiedDate(currentDateTime);

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
            customerMasterDto.setCustomerStatus(customerMaster.getCustomerStatus());
            return customerMasterDto;
        }).collect(Collectors.toList());
        return customerMasterDtoList;
    }

    @Override
    public List<String> getAllCustomerNames() {
        List<String> customerMasterList = customerMasterRepository.findListCustomerName();
        return customerMasterList.stream()
                .distinct()//returns the Customer name
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAddressOfCustomer(String name) {
        List<String> getAddress = customerMasterRepository.findCustomerAddressByCustomerName(name);
        return getAddress;
    }
    @Override
    public CustomerMasterDto getCustomerById(long id) {
        if((Long)id==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Id is not given");
        }
        CustomerMaster customer = customerMasterRepository.findByCustomerId(id);
        if(customer==null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Customer is not available with: "+id);
        }
        ModelMapper modelMapper = new ModelMapper();
        CustomerMasterDto customerMasterDto = modelMapper.map(customer, CustomerMasterDto.class);
        return customerMasterDto;
    }

    @Override
    public String updateCustomerById(CustomerRequest customerRequest, long id,String userId) {
        try{
            CustomerMaster customerMaster = customerMasterRepository.findByCustomerId(id);
            if(customerMaster==null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Customer is not available with given Id "+id);
            }
            // Check if the new email or contact number already exists for other users
            boolean customerExist = customerMasterRepository.existsByCustomerEmailAndCustomerIdNot(
                    customerRequest.getCustomerEmail(), id
            );
            if (customerExist) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "EmailId is exists with another user");
            }
            customerMaster.setCustomerName(customerRequest.getCustomerName());
            customerMaster.setCustomerEmail(customerRequest.getCustomerEmail());
            customerMaster.setCustomerAddressLine1(customerRequest.getCustomerAddressLine1());
            customerMaster.setCustomerAddressLine2(customerRequest.getCustomerAddressLine2());
            customerMaster.setCity(customerRequest.getCity());
            customerMaster.setState(customerRequest.getState());
            customerMaster.setCountry(customerRequest.getCountry());
            customerMaster.setZip(customerRequest.getZip());
            customerMaster.setCustomerContactNo(customerRequest.getCustomerContactNo());
            LocalDateTime currentDateTime = LocalDateTime.now();
            customerMaster.setCustomerModifiedBy(userId);
            customerMaster.setCustomerModifiedDate(currentDateTime);
            customerMasterRepository.save(customerMaster);
            return "Customer Update Succesfully";
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to Update Customer", e);
        }

    }
    @Override
    public String deleteCustomerById(long id) {
        CustomerMaster byCustomerId = customerMasterRepository.findByCustomerId(id);
        if(byCustomerId==null){
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST,"Id is not found ");
        }
        byCustomerId.setCustomerStatus("INACTIVE");
        customerMasterRepository.save(byCustomerId);
        return "Deleted Succesfully";
    }

    @Override
    public String activeCustomerId(Long customerId) {
        CustomerMaster byCustomerId = customerMasterRepository.findByCustomerId(customerId);
        if(byCustomerId==null){
            throw new ResponseStatusException( HttpStatus.BAD_REQUEST,"Id is not found ");
        }
        byCustomerId.setCustomerStatus("ACTIVE");
        customerMasterRepository.save(byCustomerId);
        return "Active Successfully";
    }
}
