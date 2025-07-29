package com.siemens.proton.hackx.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.proton.hackx.model.GridPowerDTO;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import com.siemens.proton.hackx.service.GridPowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class GridPowerServiceImpl implements GridPowerService {

    @Autowired
    private FrequencyRiskService frequencyRiskService;

    @Override
    public GridPowerDTO getIncomingPowerToGrid(String latitude, String longitude) {
        APIResponse weatherAPIResponse = frequencyRiskService.getWeatherData(latitude, longitude);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map dataMap = objectMapper.convertValue(weatherAPIResponse.getData(), Map.class);
            Map<String, Object> currentMap = (Map<String, Object>) dataMap.get("current");

            double tempC = Double.parseDouble(currentMap.get("temp_c").toString());
            double windKph = Double.parseDouble(currentMap.get("wind_kph").toString());
            double uv = Double.parseDouble(currentMap.get("uv").toString());

            // Power Calculation Logic
            double windSpeedMs = windKph / 3.6;
            double solarIrradiance = uv * 120; // estimate from UV index
            double airDensity = 1.225;
            double windArea = 10;
            double windCp = 0.4;
            double solarArea = 10;
            double solarEfficiency = 0.18;
            double tempCoefficient = -0.005;

            double windPower = 0.5 * airDensity * windArea * Math.pow(windSpeedMs, 3) * windCp;
            double tempAdjustment = 1 + tempCoefficient * (tempC - 25);
            double solarPower = solarIrradiance * solarArea * solarEfficiency * tempAdjustment;
            double totalPower = 0.5 * windPower + 0.5 * solarPower;

            double frequency = getFrequency(totalPower);
            String frequencyHealth = getFrequencyHealth(frequency);

            return GridPowerDTO.builder().solarPower(solarPower).windPower(windPower).totalPower(totalPower)
                    .gridFrequency(frequency).frequencyHealth(frequencyHealth).build();
        } catch (Exception e) {
            log.error("Error processing weather data: {}", e.getMessage(), e);
        }
        return null;
    }

    private double getFrequency(double totalPower) {
        // Hardcoded normal demand power for the grid
        double demandPower = 5000.0; // in Watts

        // Frequency estimation logic
        double frequency;
        if (totalPower == demandPower) {
            frequency = 50.0;
        } else if (totalPower < demandPower) {
            frequency = 49.0 + (totalPower / demandPower);  // drop frequency if under-supplied
        } else {
            frequency = 50.0 + ((totalPower - demandPower) / demandPower) * 0.5; // rise slightly on over-supply
        }
        return frequency;
    }

    private String getFrequencyHealth(double frequency) {
        // Frequency health status
        String frequencyHealth;
        if (frequency >= 49.5 && frequency <= 50.5) {
            frequencyHealth = "HEALTHY";
        } else if (frequency >= 49.0 && frequency < 49.5) {
            frequencyHealth = "WARNING";
        } else {
            frequencyHealth = "CRITICAL";
        }
        return frequencyHealth;
    }


}
