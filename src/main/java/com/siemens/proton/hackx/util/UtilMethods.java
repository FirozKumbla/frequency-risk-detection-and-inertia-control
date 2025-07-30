package com.siemens.proton.hackx.util;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.response.DataDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UtilMethods {

    public Map<String, Map<String, List<DataDto>>> calculateWindPower(Map<String, Object> weatherData, LocationConfigModel config) {
        Integer numOfWindTurbines = config.getWindMillCount();
        Map<String, Map<String, List<DataDto>>> windPowerData = new LinkedHashMap<>();
        Map<String, List<DataDto>> dataMap = new LinkedHashMap<>();
        List<DataDto> resultList = new ArrayList<>();

        // Wind turbine constants
        double ratedPowerKW = 2000.0; // 2 MW in kW
        double cutIn = 3.5;
        double rated = 13.0;
        double cutOut = 25.0;

        Map<String, Object> data = (Map<String, Object>) weatherData.get("data");
        Map<String, Object> forecast = (Map<String, Object>) data.get("forecast");
        List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecast.get("forecastday");

        for (Map<String, Object> day : forecastDays) {
            String date = (String) day.get("date");
            List<Map<String, Object>> hours = (List<Map<String, Object>>) day.get("hour");

            List<DataDto> hourlyList = new ArrayList<>();

            for (Map<String, Object> hourData : hours) {
                String time = (String) hourData.get("time");
                double windKph = ((Number) hourData.get("wind_kph")).doubleValue();
                double windSpeed = windKph / 3.6; // Convert kph to m/s

                double powerPerTurbine;
                if (windSpeed < cutIn || windSpeed > cutOut) {
                    powerPerTurbine = 0;
                } else if (windSpeed < rated) {
                    powerPerTurbine = ratedPowerKW * Math.pow((windSpeed - cutIn) / (rated - cutIn), 3);
                } else {
                    powerPerTurbine = ratedPowerKW;
                }

                double totalPower = powerPerTurbine * numOfWindTurbines;
                hourlyList.add(new DataDto(time, totalPower));
            }
            dataMap.put(date, hourlyList);
        }
        windPowerData.put("windPower", dataMap);
        return windPowerData;
    }

}
