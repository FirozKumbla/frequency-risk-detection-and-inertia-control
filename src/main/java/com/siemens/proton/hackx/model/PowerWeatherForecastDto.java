package com.siemens.proton.hackx.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PowerWeatherForecastDto {
    private String timestamp;
    private double windPower;
    private double solarPower;
    private double totalPower;
}
