package com.siemens.proton.hackx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GridPowerDTO {
    private Double windPower;
    private Double solarPower;
    private Double totalPower;
    private Double gridFrequency;
    private String frequencyHealth;
}
