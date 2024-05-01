package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.ParameterMaster;
import com.weighbridge.admin.repsitories.ParameterMasterRepository;
import com.weighbridge.admin.services.ParameterMasterService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
public class ParameterMasterServiceImpl implements ParameterMasterService {
    private final ParameterMasterRepository parameterMasterRepository;

    public ParameterMasterServiceImpl(ParameterMasterRepository parameterMasterRepository) {
        this.parameterMasterRepository = parameterMasterRepository;
    }

    @Override
    public long createParameter(String parameterName) {
        boolean isPresent = parameterMasterRepository.existsByParameterName(parameterName);
        if (isPresent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter is already exists");
        }

        ParameterMaster parameterMaster = new ParameterMaster();
        parameterMaster.setParameterName(parameterName);

        try {
            ParameterMaster savedParameter = parameterMasterRepository.save(parameterMaster);
            return savedParameter.getParameterId();
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save parameter", e);
        }
    }

    @Override
    public List<String> getAllParameterNames() {
        try {
            List<String> allParameterNames = parameterMasterRepository.findAllParameterNames();
            return allParameterNames != null ? allParameterNames : Collections.emptyList();
        } catch (DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch parameter names", e);
        }

    }

}
