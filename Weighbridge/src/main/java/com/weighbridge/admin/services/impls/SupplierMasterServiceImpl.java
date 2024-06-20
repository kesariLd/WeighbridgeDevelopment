package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.dtos.SupplierMasterDto;
import com.weighbridge.admin.entities.SupplierMaster;
import com.weighbridge.admin.entities.SupplierMaster;
import com.weighbridge.admin.exceptions.SessionExpiredException;
import com.weighbridge.admin.payloads.SupplierRequest;
import com.weighbridge.admin.repsitories.SupplierMasterRepository;
import com.weighbridge.admin.services.SupplierMasterService;
import com.weighbridge.admin.dtos.SupplierMasterDto;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierMasterServiceImpl implements SupplierMasterService {

    @Autowired
    private SupplierMasterRepository supplierMasterRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ModelMapper modelMapper;
    @Override
    public SupplierMasterDto createSupplier(SupplierMasterDto supplierMasterDto,String userId) {
        try {
            // Check if the supplier contact number or email already exists
            boolean exists = supplierMasterRepository.existsBySupplierContactNoOrSupplierEmail(
                    supplierMasterDto.getSupplierContactNo(),
                    supplierMasterDto.getSupplierEmail()

            );
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Id or Contact No is already taken");
            }

            if (supplierMasterDto == null) {
                throw new ResourceNotFoundException("supplierMasterDto is null");
            }
            SupplierMaster newSupplierMaster = modelMapper.map(supplierMasterDto, SupplierMaster.class);
           /* HttpSession session = httpServletRequest.getSession();
            String userId = String.valueOf(session.getAttribute("userId"));*/
            newSupplierMaster.setSupplierCreatedBy(userId);
            newSupplierMaster.setSupplierCreatedDate(LocalDateTime.now());
            newSupplierMaster.setSupplierModifiedBy(userId);
            newSupplierMaster.setSupplierModifiedDate(LocalDateTime.now());
            newSupplierMaster.setSupplierStatus("ACTIVE");
            SupplierMaster savedSupplier = supplierMasterRepository.save(newSupplierMaster);
            SupplierMasterDto mappedSupplierMasterDto = modelMapper.map(savedSupplier, SupplierMasterDto.class);
            return mappedSupplierMasterDto;
        } catch (Exception e) {
            // Handle any other exceptions and rethrow as necessary
            throw new RuntimeException("Failed to create supplier", e);
        }
    }

    @Override
    public List<SupplierMasterDto> getAllSupplier() {
        List<SupplierMaster> supplierMasterList = supplierMasterRepository.findAll();

        return supplierMasterList.stream().map(supplier -> modelMapper.map(supplier, SupplierMasterDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<String> getAllSupplierAsString() {
        List<String> supplierMasterList = supplierMasterRepository.findListSupplierName();
        // Map SupplierMaster objects to their names and collect into a list
        List<String> supplierNames = supplierMasterList.stream()
                .distinct()// Assuming getSupplierName() returns the supplier name
                .collect(Collectors.toList());
        return supplierNames;
    }

    @Override
    public List<String> getAddressOfSupplier(String supplierName) {

        List<String> supplierAddressBySupplierName = supplierMasterRepository.findSupplierAddressBySupplierName(supplierName);
        return supplierAddressBySupplierName;
    }

    @Override
    public List<String> getSupplierAddressBySupplierName(String supplierName) {
        List<String> supplierAddresses = supplierMasterRepository.findSupplierAddressLine1BySupplierName(supplierName);
        return supplierAddresses;
    }

    @Override
    public SupplierMasterDto getSupplierById(long id) {
        if ((Long) id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not given");
        }
        SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(id);
        if (supplierMaster == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier is not available with: " + id);
        }
        ModelMapper modelMapper = new ModelMapper();
        SupplierMasterDto supplierMasterDto = modelMapper.map(supplierMaster, SupplierMasterDto.class);
        return supplierMasterDto;
    }

    @Override
    public String updateSupplierById(SupplierRequest supplierRequest, long id,String userId) {
        try {
            SupplierMaster supplierMaster = supplierMasterRepository.findBySupplierId(id);
            if (supplierMaster == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier is not available with given Id " + id);
            }
            // Check if the new email or contact number already exists for other users
            boolean SupplierExist = supplierMasterRepository.existsBySupplierEmailAndSupplierIdNot(
                    supplierRequest.getSupplierEmail(), id
            );
            if (SupplierExist) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "EmailId is exists with another user");
            }
            supplierMaster.setSupplierName(supplierRequest.getSupplierName());
            supplierMaster.setSupplierEmail(supplierRequest.getSupplierEmail());
            supplierMaster.setSupplierAddressLine1(supplierRequest.getSupplierAddressLine1());
            supplierMaster.setSupplierAddressLine2(supplierRequest.getSupplierAddressLine2());
            supplierMaster.setCity(supplierRequest.getCity());
            supplierMaster.setState(supplierRequest.getState());
            supplierMaster.setCountry(supplierRequest.getCountry());
            supplierMaster.setZip(supplierRequest.getZip());
            supplierMaster.setSupplierContactNo(supplierRequest.getSupplierContactNo());
          /*  HttpSession session = httpServletRequest.getSession();
            if (session == null && session.getAttribute("userID") == null) {
                throw new SessionExpiredException("Session Expired ! Login again");
            }*/
           // String userId = session.getAttribute("userId").toString();
            LocalDateTime currentDateTime = LocalDateTime.now();
            supplierMaster.setSupplierModifiedBy(userId);
            supplierMaster.setSupplierModifiedDate(currentDateTime);
            supplierMasterRepository.save(supplierMaster);
            return "Supplier Update Succesfully";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to Update Supplier", e);
        }

    }

    @Override
    public String deleteSupplierById(long id) {
        SupplierMaster bySupplierId = supplierMasterRepository.findBySupplierId(id);
        if (bySupplierId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not found ");
        }
        bySupplierId.setSupplierStatus("INACTIVE");
        supplierMasterRepository.save(bySupplierId);
        return "Deleted Succesfully";
    }

    @Override
    public String activeSupplier(Long supplierId) {
        SupplierMaster bySupplierId = supplierMasterRepository.findBySupplierId(supplierId);
        if (bySupplierId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not found ");
        }
        bySupplierId.setSupplierStatus("ACTIVE");
        supplierMasterRepository.save(bySupplierId);
        return "Active Successfully";
    }

}
