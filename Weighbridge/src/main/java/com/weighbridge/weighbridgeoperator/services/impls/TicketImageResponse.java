package com.weighbridge.weighbridgeoperator.services.impls;

import com.weighbridge.weighbridgeoperator.payloads.TicketResponse;
import lombok.Data;

import java.util.Map;

@Data
public class TicketImageResponse {

    private TicketResponse ticketResponse;

    private Map<String, byte[]> inImagesMap ;
    private Map<String, byte[]> outImagesMap ;

}
