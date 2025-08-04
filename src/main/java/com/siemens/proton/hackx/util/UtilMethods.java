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
        double ratedPowerKW = 2000.0;
        double cutIn = 3.5, rated = 13.0, cutOut = 25.0;
        double ratedVoltage = 400.0; // V
        double gridFrequency = 50.0; // Hz

        List<DataDto> hourlyList = new ArrayList<>();
        for (int i = 0; i < windSpeeds.size(); i++) {
            String time = timeStamps.get(i);
            double windKph = windSpeeds.get(i);
            double windSpeed = windKph / 3.6;

            double powerPerTurbineKW;
            double voltage;
            if (windSpeed < cutIn || windSpeed > cutOut) {
                powerPerTurbineKW = 0;
                voltage = 0;
            } else if (windSpeed < rated) {
                powerPerTurbineKW = ratedPowerKW * Math.pow((windSpeed - cutIn) / (rated - cutIn), 3);
                voltage = ratedVoltage * (windSpeed / rated); // scale voltage up to rated
            } else {
                powerPerTurbineKW = ratedPowerKW;
                voltage = ratedVoltage;
            }
            double totalPowerMW = (powerPerTurbineKW * numOfWindTurbines) / 1000.0;

            DataDto dto = new DataDto();
            dto.setTime(time);
            dto.setValue(Math.round(totalPowerMW * 100.0) / 100.0);
            dto.setVoltage((Math.round(voltage * 10.0) / 10.0) / 1000); // 1 decimal in KV
            hourlyList.add(dto);
        }
        return hourlyList;
    }

    @Async
    public List<DataDto> calculateSolarEnergy(List<Double> hourlyTemps,
                                              List<Double> uvIndex,
                                              int numberOfPanels,
                                              List<String> timeStamps) {
        double panelVoltage = 36.0; // V per panel
        double gridFrequency = 50.0; // Hz

        List<DataDto> dataDtos = new LinkedList<>();
        for (int i = 0; i < hourlyTemps.size(); i++) {
            DataDto dataDto = new DataDto();
            double tempC = hourlyTemps.get(i);
            double uv = uvIndex.get(i);
            String time = timeStamps.get(i);

            double derateFactor = (tempC <= 25) ? 1.0 : (1 - TEMP_DERATE_PER_C * (tempC - 25));
            double effectivePowerPerPanel = RATED_POWER_PER_PANEL * derateFactor;
            double outputPerPanel = effectivePowerPerPanel * (uv / MAX_UV_INDEX);
            double totalOutputMW = (outputPerPanel * numberOfPanels) / 1_000_000.0;

            dataDto.setTime(time);
            dataDto.setValue(Math.round(totalOutputMW * 100.0) / 100.0);
            dataDto.setVoltage((panelVoltage * numberOfPanels) / 1000);  // In KV simple: all in series
            //dataDto.setFrequency(gridFrequency);
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
            double totalVoltage = solarGraph.get(i).getVoltage() + windGraph.get(i).getVoltage();
            totalPowerGraph.add(new DataDto(timeStamps.get(i), totalPower, totalVoltage));
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

                double mockDemandPower = (100 + Math.random() * 50) / 1000.0; // Random between 100 and 150

                DataDto dataPoint = new DataDto();
                dataPoint.setTime(timestamp.format(formatter));
                dataPoint.setValue(mockDemandPower);

                totalPowerGraph.add(dataPoint);
            }
        }
        return totalPowerGraph;
    }
}
