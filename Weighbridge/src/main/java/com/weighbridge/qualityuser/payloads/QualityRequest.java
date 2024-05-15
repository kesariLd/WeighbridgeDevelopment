package com.weighbridge.qualityuser.payloads;

import lombok.Data;

/**
 * This class represents a request payload for creating a quality transaction.
 */
@Data
public class QualityRequest {

    private Double moisture;
    private Double vm;
    private Double ash;
    private Double fc;
    private Double size;
    private Double fe_m;
    private Double fe_t;
    private Double mtz;
    private Double carbon;
    private Double sulphur;
    private Double non_mag;
    private Double loi;

}
