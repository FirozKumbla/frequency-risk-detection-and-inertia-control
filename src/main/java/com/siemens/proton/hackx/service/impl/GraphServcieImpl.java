package com.siemens.proton.hackx.service.impl;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.response.DataDto;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import com.siemens.proton.hackx.service.GraphServcie;
import com.siemens.proton.hackx.util.UtilMethods;
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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
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

    @Autowired
    private UtilMethods utilMethods;

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
                List<Double> windSpeeds = new ArrayList<>();

                // Assuming the hourly data is available in the "hour" key
                List<Map<String, Object>> hourlyData = (List<Map<String, Object>>) day.get("hour");
                for (Map<String, Object> hour : hourlyData) {
                    hourlyTemps.add((Double) hour.get("temp_c"));
                    uvIndex.add(Double.valueOf(hour.get("uv").toString()));
                    timeStamps.add((String) hour.get("time"));
                    windSpeeds.add(Double.valueOf(hour.get("wind_kph").toString()));
                }

                // Calculate solar energy output
                List<DataDto> solarGraph = utilMethods.calculateSolarEnergy(hourlyTemps, uvIndex, config.getSolarPanelCount(), timeStamps);
                List<DataDto> windGraph = utilMethods.calculateWindPower(windSpeeds, config.getWindMillCount(), timeStamps);

                graphData.put(date, Map.of(
                        "solarEnergy", solarGraph,
                        "windEnergy", windGraph,
                        "totalEnergy", utilMethods.getTotalPowerGraph(timeStamps, solarGraph, windGraph)
                ));

                System.out.println(getPredication(graphData));

            }
            return graphData;
        }

        return Collections.emptyMap();
    }


    public String getPredication(Map<String, Map<String, List<DataDto>>> graphData) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant that provides energy predictions based on solar and wind data."),
                Map.of("role", "user", "content", "Given this input, predict frequency dips, RoCoF, grid health and inertia need and provide the response in the given List of FreqPredictionDTO class public class FreqPredictionDTO { private String timestamp; private double predictedFreq; private double rocOfFreq; private String gridHealth; private boolean syntheticInertiaRequired; private boolean triggerControlCommand; } format without any additional text: " + graphData)
        ));

        try {
            disableSSLVerification();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpEntity entity = new HttpEntity(requestBody, new HttpHeaders() {{
            set("Authorization", "Bearer " + AI_API_KEY);
            set("Content-Type", "application/json");
        }});
        ResponseEntity<String> predectedResponse = restTemplate.exchange("https://api.groq.com/openai/v1/chat/completions", HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {
        });
        return predectedResponse.getBody();
    }

    private void disableSSLVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Disable host name verification
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
