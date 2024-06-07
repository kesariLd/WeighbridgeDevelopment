package com.weighbridge.management.payload;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ManagementGateEntryList {
    private List<ManagementGateEntryTransactionResponse> transactions;
    private Integer totalPages;
    private Long totalElements;
}