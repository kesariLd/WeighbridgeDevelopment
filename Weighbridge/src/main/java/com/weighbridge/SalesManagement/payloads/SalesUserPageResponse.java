package com.weighbridge.SalesManagement.payloads;

import lombok.Data;

import java.util.List;

@Data
public class SalesUserPageResponse {
    private List<?> sales;
    private Long totalPage;
    private Long totalElement;
}