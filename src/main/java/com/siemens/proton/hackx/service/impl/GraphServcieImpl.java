package com.siemens.proton.hackx.service.impl;

import com.siemens.proton.hackx.response.APIResponse;
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

import java.util.Map;

import static com.siemens.proton.hackx.constant.ApplicationConstant.*;

@Service
public class GraphServcieImpl implements GraphServcie {

    @Value("${weather.url}")
    private String weatherUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public APIResponse getGraphData(String latitude, String longitude) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherUrl).pathSegment(FORECAST_WEATHER_API_URI)
                .queryParam("q", latitude.trim() + "," + longitude.trim())
                .queryParam("key", WEATHER_API_KEY)
                .queryParam("days", 7);  // Assuming we want a 7-day forecast
        // Assuming we want a 7-day forecast
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
}
