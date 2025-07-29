package com.siemens.proton.hackx.service.impl;

import com.siemens.proton.hackx.repository.FrequencyRiskRepository;
import com.siemens.proton.hackx.model.GridConfigModel;
import com.siemens.proton.hackx.repository.GridConfigRepository;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.FrequencyRiskService;
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

import java.util.Map;

import static com.siemens.proton.hackx.constant.ApplicationConstant.*;

@Service
public class FrequencyRiskServiceImpl implements FrequencyRiskService {

    @Value("${weather.url}")
    private String weatherUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GridConfigRepository gridConfigRepository;

    @Override
    public APIResponse getWeatherData(String latitude, String longitude) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherUrl).pathSegment(CURRENT_WEATHER_API_URI)
                .queryParam("q", latitude.trim() + "," + longitude.trim())
                .queryParam("key", WEATHER_API_KEY);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<Map<String, Object>>() {
        });

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> weatherData = response.getBody();
            return APIResponse.builder().status(response.getStatusCode().value()).data(weatherData).build();
        }
        return APIResponse.builder()
                .status(response.getStatusCode().value())
                .message("Failed to fetch weather data")
                .build();
    }

    @Override
    public APIResponse gridConfiguration(GridConfigModel gridConfigRequest) {
        gridConfigRequest = gridConfigRepository.save(gridConfigRequest);
        return APIResponse.builder()
                .status(201)
                .message("Grid configuration saved successfully")
                .data(gridConfigRequest)
                .build();
    }

    @Override
    public APIResponse getGridConfiguration(Integer id) {
        GridConfigModel gridConfig = gridConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grid configuration not found with id: " + id));
        return APIResponse.builder()
                .status(200)
                .message("Grid configuration retrieved successfully")
                .data(gridConfig)
                .build();
    }

    @Override
    public APIResponse getAllGridConfigurations() {
        Iterable<GridConfigModel> gridConfigs = gridConfigRepository.findAll();
        return APIResponse.builder()
                .status(200)
                .message("All grid configurations retrieved successfully")
                .data(gridConfigs)
                .build();
    }
}
