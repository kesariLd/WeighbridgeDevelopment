package com.weighbridge.admin.payloads;

import lombok.Data;

import java.util.List;

@Data
public class GetAllUsersPaginationResponse {
    private List<UserResponse> users;
    private Integer totalPages;
    private Long totalElements;
}
