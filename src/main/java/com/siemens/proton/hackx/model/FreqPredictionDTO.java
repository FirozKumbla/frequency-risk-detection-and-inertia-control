package com.siemens.proton.hackx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreqPredictionDTO {
    private String timestamp;
    private double predictedFreq;
    private double rocOfFreq;
    private String gridHealth;
    private boolean syntheticInertiaRequired;
    private boolean triggerControlCommand;
}
