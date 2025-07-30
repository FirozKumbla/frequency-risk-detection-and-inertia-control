package com.siemens.proton.hackx.service.impl;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.response.DataDto;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import com.siemens.proton.hackx.service.GraphServcie;
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

import java.util.List;
import java.util.Map;

import static com.siemens.proton.hackx.constant.ApplicationConstant.*;

@Service
public class GraphServcieImpl implements GraphServcie {

    @Value("${weather.url}")
    private String weatherUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FrequencyRiskService frequencyRiskService;

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
            processWeatherDataForGraph(weatherData, config);

            return APIResponse.builder().status(response.getStatusCode().value()).data(weatherData).build();
        }
        return APIResponse.builder()
                .status(response.getStatusCode().value())
                .message("Failed to fetch weather data")
                .build();
    }

    private Map<String, List<DataDto>> processWeatherDataForGraph(Map<String, Object> weatherData, LocationConfigModel config) {

        return null;

    }


}
