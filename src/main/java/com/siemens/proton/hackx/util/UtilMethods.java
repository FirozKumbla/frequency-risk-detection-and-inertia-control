package com.siemens.proton.hackx.util;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.response.DataDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class UtilMethods {

    private static final double RATED_POWER_PER_PANEL = 300.0; // in Watts
    private static final double TEMP_DERATE_PER_C = 0.005;
    private static final double MAX_UV_INDEX = 12.0;

    @Async
    public List<DataDto> calculateWindPower(List<Double> windSpeeds, int numOfWindTurbines, List<String> timeStamps) {
        Map<String, Map<String, List<DataDto>>> windPowerData = new LinkedHashMap<>();
        // Wind turbine constants
        double ratedPowerKW = 2000.0; // 2 MW in kW
        double cutIn = 3.5;
        double rated = 13.0;
        double cutOut = 25.0;

        List<DataDto> hourlyList = new ArrayList<>();

        for (int i = 0; i < windSpeeds.size(); i++) {
            String time = timeStamps.get(i);
            double windKph = windSpeeds.get(i);
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

        return hourlyList;
    }

    @Async
    public List<DataDto> calculateSolarEnergy(List<Double> hourlyTemps,
                                              List<Double> uvIndex,
                                              int numberOfPanels,
                                              List<String> timeStamps) {
        Map<String, Double> energyOutput = new LinkedHashMap<>();
        List<DataDto> dataDtos = new LinkedList<>();

        for (int i = 0; i < hourlyTemps.size(); i++) {
            DataDto dataDto = new DataDto();
            double tempC = hourlyTemps.get(i);
            double uv = uvIndex.get(i);
            String time = timeStamps.get(i);

            // No derating if temperature ≤ 25°C
            double derateFactor = (tempC <= 25) ? 1.0 : (1 - TEMP_DERATE_PER_C * (tempC - 25));
            double effectivePowerPerPanel = RATED_POWER_PER_PANEL * derateFactor;

            // Scale power based on UV index
            double outputPerPanel = effectivePowerPerPanel * (uv / MAX_UV_INDEX);

            // Total power output for all panels
            double totalOutput = outputPerPanel * numberOfPanels;

            // Round to 2 decimal places
            energyOutput.put(time, Math.round(totalOutput * 100.0) / 100.0);
            dataDto.setTime(time);
            dataDto.setValue(Math.round(totalOutput * 100.0) / 100.0);

            dataDtos.add(dataDto);
        }

        return dataDtos;
    }

    public List<DataDto> getTotalPowerGraph(List<String> timeStamps, List<DataDto> solarGraph, List<DataDto> windGraph) {
        // get total power from solar and wind for total power graph
        List<DataDto> totalPowerGraph = new ArrayList<>();
        for (int i = 0; i < timeStamps.size(); i++) {
            double solarPower = solarGraph.get(i).getValue();
            double windPower = windGraph.get(i).getValue();
            double totalPower = solarPower + windPower;
            totalPowerGraph.add(new DataDto(timeStamps.get(i), totalPower));
        }
        return totalPowerGraph;
    }

    public List<DataDto> getDemandPowerGraph(int days) {
        // get total demand power - mocked data
        List<DataDto> totalPowerGraph = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int day = 0; day < days; day++) {
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime timestamp = now.minusDays(days - 1 - day).withHour(hour).withMinute(0).withSecond(0).withNano(0);

                double mockDemandPower = 100 + Math.random() * 50; // Random between 100 and 150

                DataDto dataPoint = new DataDto();
                dataPoint.setTime(timestamp.format(formatter));
                dataPoint.setValue(mockDemandPower);

                totalPowerGraph.add(dataPoint);
            }
        }
        return totalPowerGraph;
    }
}
