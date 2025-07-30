package com.siemens.proton.hackx.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FreqPredictionDTO {

    private String timestamp;
    private double predictedFreq;
    private double rocOfFreq;
    private String gridHealth;
    private boolean syntheticInertiaRequired;
    private boolean triggerControlCommand;

}
