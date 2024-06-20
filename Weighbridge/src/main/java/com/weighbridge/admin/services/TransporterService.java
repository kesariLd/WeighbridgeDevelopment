package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.TransporterDto;
import com.weighbridge.admin.payloads.TransporterRequest;

import java.util.List;

/**
 * This Service interface for managing transporter master data.
 */
public interface TransporterService {
    /**
     * Saved a transporter using provided payload.
     *
     * @param transporterRequest The payload containing the transporter to be saved.
     * @return String containing a successful message.
     */
    String addTransporter(TransporterRequest transporterRequest,String userId);

    /**
     * Get all transporters
     * @return List of Strings containing all transporters
     */
    public List<String> getAllTransporterNames();

    List<TransporterDto> getAllTransporter();

    TransporterDto getTransporterById(Long transporterId);

    String updateTransporterById(Long transporterId, TransporterDto transporterDto,String userId);

    boolean deactivateTransporterById(Long transporterId);

    boolean activateTransporterById(Long transporterId);
}
