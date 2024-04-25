package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.SupplierMaster;
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
    public SupplierMasterDto createSupplier(SupplierMasterDto supplierMasterDto) {
        try {
            // Check if the supplier contact number or email already exists
            boolean exists = supplierMasterRepository.existsBySupplierContactNoOrSupplierEmail(
                    supplierMasterDto.getSupplierContactNo(),
                    supplierMasterDto.getEmailId()

            );
            if (exists) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email Id or Contact No is already taken");
            }

            if (supplierMasterDto == null) {
                throw new ResourceNotFoundException("supplierMasterDto is null");
            }
            SupplierMaster newSupplierMaster = modelMapper.map(supplierMasterDto, SupplierMaster.class);
            HttpSession session = httpServletRequest.getSession();
            String userId = String.valueOf(session.getAttribute("userId"));
            newSupplierMaster.setSupplierCreatedBy(userId);
            newSupplierMaster.setSupplierCreatedDate(LocalDateTime.now());
            newSupplierMaster.setSupplierModifiedBy(userId);
            newSupplierMaster.setSupplierModifiedDate(LocalDateTime.now());
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
        List<SupplierMaster> supplierMasterList = supplierMasterRepository.findAll();
// Map SupplierMaster objects to their names and collect into a list
        List<String> supplierNames = supplierMasterList.stream()
                .map(SupplierMaster::getSupplierName) // Assuming getSupplierName() returns the supplier name
                .collect(Collectors.toList());
        return supplierNames;
    }


}
