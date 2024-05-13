package com.weighbridge.qualityuser.payloads;

import lombok.Data;

/**
 * This class represents a request payload for creating a quality transaction.
 */
@Data
public class QualityRequest {

    /**
     * Moisture content of the material.
     */
    private double moisture;

    private double vm;

    /**
     * Ash content of the material.
     */
    private double ash;

    /**
     * Fixed carbon content of the material.
     */
    private double fc;

    /**
     * Size of the material in 20mm category.
     */
    private double size_20mm;

    /**
     * Size of the material in 0.3mm category.
     */
    private double size_03mm;

    /**
     * Iron content of the material.
     */
    private double fe_t;

    /**
     * Loss Of Ignition content of the material.
     */
    private double loi;
}
