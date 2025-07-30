package com.siemens.proton.hackx.service.impl;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.response.DataDto;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import com.siemens.proton.hackx.service.GraphServcie;
import org.h2.util.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

import static com.siemens.proton.hackx.constant.ApplicationConstant.*;

@Service
public class GraphServcieImpl implements GraphServcie {

    @Value("${weather.url}")
    private String weatherUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FrequencyRiskService frequencyRiskService;


    // Constants
    private static final double RATED_POWER_PER_PANEL = 300.0; // in Watts
    private static final double TEMP_DERATE_PER_C = 0.005;
    private static final double MAX_UV_INDEX = 12.0;

    @Override
    public APIResponse getGraphData(int locationId, int days) {

        APIResponse locationConfig = frequencyRiskService.getGridConfiguration(locationId);
        if (locationConfig == null || locationConfig.getData() == null) {
            return APIResponse.builder()
                    .status(404)
                    .message("Location configuration not found")
                    .build();
        }

        LocationConfigModel config = (LocationConfigModel) locationConfig.getData();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherUrl).pathSegment(FORECAST_WEATHER_API_URI)
                .queryParam("q", config.getLatitude().trim() + "," + config.getLongitude().trim())
                .queryParam("key", WEATHER_API_KEY)
                .queryParam("days", days);  // Assuming we want a 7-day forecast
        // Assuming we want a 7-day forecast
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<Map<String, Object>>() {
        });

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> weatherData = response.getBody();

            // Process the weather data as needed for graphing
            Map<String, Map<String, List<DataDto>>> data = processWeatherDataForGraph(weatherData, config);

            return APIResponse.builder().status(response.getStatusCode().value()).data(data).build();
        }
        return APIResponse.builder()
                .status(response.getStatusCode().value())
                .message("Failed to fetch weather data")
                .build();
    }

    private Map<String, Map<String, List<DataDto>>> processWeatherDataForGraph(Map<String, Object> weatherData, LocationConfigModel config) {

        Map<String, Object> forecastMap = (Map<String, Object>) weatherData.get("forecast");
        if (forecastMap != null) {
            List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecastMap.get("forecastday");
            Map<String, Map<String, List<DataDto>>> graphData = new LinkedHashMap<>();

            for (Map<String, Object> day : forecastDays) {
                String date = (String) day.get("date");

                List<Double> hourlyTemps = new ArrayList<>();
                List<Double> uvIndex = new ArrayList<>();
                List<String> timeStamps = new ArrayList<>();

                // Assuming the hourly data is available in the "hour" key
                List<Map<String, Object>> hourlyData = (List<Map<String, Object>>) day.get("hour");
                for (Map<String, Object> hour : hourlyData) {
                    hourlyTemps.add((Double) hour.get("temp_c"));
                    uvIndex.add(Double.valueOf(hour.get("uv").toString()));
                    timeStamps.add((String) hour.get("time"));
                }

                // Calculate solar energy output
                List<DataDto> dataDtos = calculateSolarEnergy(hourlyTemps, uvIndex, config.getSolarPanelCount(), timeStamps);
                graphData.put(date, Collections.singletonMap("solarEnergy", dataDtos));
            }
            return graphData;
        }

        return Collections.emptyMap();
    }


    public static List<DataDto> calculateSolarEnergy(List<Double> hourlyTemps,
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
}
