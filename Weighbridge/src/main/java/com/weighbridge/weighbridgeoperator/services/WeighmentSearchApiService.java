package com.weighbridge.weighbridgeoperator.services;

import com.weighbridge.weighbridgeoperator.dto.WeighbridgeOperatorSearchCriteria;
import com.weighbridge.weighbridgeoperator.payloads.WeighbridgePageResponse;
import com.weighbridge.weighbridgeoperator.payloads.WeighmentTransactionResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

public interface WeighmentSearchApiService {
    WeighmentTransactionResponse getByTicketNo(Integer ticketNo);

    WeighbridgePageResponse getAllBySearchFields(WeighbridgeOperatorSearchCriteria criteria, Pageable pageable,String userId);

    WeighbridgePageResponse getAllBySearchFieldsForInprocessTransaction(WeighbridgeOperatorSearchCriteria criteria, Pageable pageable,String userId);
}