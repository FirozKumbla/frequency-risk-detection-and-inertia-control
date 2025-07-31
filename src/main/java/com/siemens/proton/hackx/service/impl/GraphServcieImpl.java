package com.siemens.proton.hackx.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.request.FreqPredictionDTO;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.response.DataDto;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import com.siemens.proton.hackx.service.GraphServcie;
import com.siemens.proton.hackx.util.UtilMethods;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

    @Value("${gork.url}")
    private String gorkUrl;

    @Value("${gork.key}")
    private String apiKey;

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

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(weatherUrl)
                .queryParam("latitude", config.getLatitude().trim())
                .queryParam("longitude", config.getLongitude().trim())
                .queryParam("hourly", "temperature_2m,wind_speed_10m,direct_radiation");
        // Assuming we want a 7-day forecast
        // Assuming we want a 7-day forecast
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<Map<String, Object>>() {
        });

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> weatherData = response.getBody();

            // Process the weather data as needed for graphing
            Map<String, Map<String, List<DataDto>>> data = processWeatherDataForGraph(weatherData, config, days);

            return APIResponse.builder().status(response.getStatusCode().value()).data(data).build();
        }
        return APIResponse.builder()
                .status(response.getStatusCode().value())
                .message("Failed to fetch weather data")
                .build();
    }

    @Async
    private Map<String, Map<String, List<DataDto>>> processWeatherDataForGraph(Map<String, Object> weatherData, LocationConfigModel config, int days) {

        Map<String, Object> forecastMap = (Map<String, Object>) weatherData.get("hourly");



        return Map.of("CurrentDay", processForeCastDataForADay(forecastMap, config));

//        if (forecastMap != null) {
//            List<Map<String, Object>> forecastDays = (List<Map<String, Object>>) forecastMap.get("forecastday");
//            Map<String, Map<String, List<DataDto>>> graphData = new LinkedHashMap<>();
//
//            for (Map<String, Object> day : forecastDays) {
//                String date = (String) day.get("date");
//
//                List<Map<String, Object>> hourlyData = (List<Map<String, Object>>) day.get("hour");
//                List<Double> hourlyTemps = new ArrayList<>();
//                List<Double> uvIndex = new ArrayList<>();
//                List<String> timeStamps = new ArrayList<>();
//                List<Double> windSpeeds = new ArrayList<>();
//
//                for (Map<String, Object> hour : hourlyData) {
//                    hourlyTemps.add((Double) hour.get("temp_c"));
//                    uvIndex.add(Double.parseDouble(hour.get("uv").toString()));
//                    timeStamps.add((String) hour.get("time"));
//                    windSpeeds.add(Double.parseDouble(hour.get("wind_kph").toString()));
//                }
//
//                List<DataDto> solarGraph = utilMethods.calculateSolarEnergy(hourlyTemps, uvIndex, config.getSolarPanelCount(), timeStamps);
//                List<DataDto> windGraph = utilMethods.calculateWindPower(windSpeeds, config.getWindMillCount(), timeStamps);
//
//                graphData.put(date, Map.of(
//                        "solarEnergy", solarGraph,
//                        "windEnergy", windGraph,
//                        "totalEnergy", utilMethods.getTotalPowerGraph(timeStamps, solarGraph, windGraph)
//                ));
//            }
//            return graphData;
//        }
    }

    private Map<String, List<DataDto>> processForeCastDataForADay(Map<String, Object> forecastMap, LocationConfigModel config) {

        List<String> timeStamps = (List<String>) forecastMap.get("time");
        List<Double> windSpeeds = (List<Double>) forecastMap.get("temperature_2m");
        List<Double> hourlyTemps = (List<Double>) forecastMap.get("wind_speed_10m");
        List<Double> uvIndex = (List<Double>) forecastMap.get("direct_radiation");

        List<DataDto> solarGraph = utilMethods.calculateSolarEnergy(hourlyTemps, uvIndex, config.getSolarPanelCount(), timeStamps);
        List<DataDto> windGraph = utilMethods.calculateWindPower(windSpeeds, config.getWindMillCount(), timeStamps);

        return Map.of(
                "solarEnergy", solarGraph,
                "windEnergy", windGraph,
                "demandEnergy", utilMethods.getDemandPowerGraph(1),
                "totalEnergy", utilMethods.getTotalPowerGraph(timeStamps, solarGraph, windGraph)
        );
    }

    private List<FreqPredictionDTO> processPredictedData(String jsonStr) {
        // Assuming jsonStr is a JSON string that needs to be parsed into a List of FreqPredictionDTO
        // For simplicity, let's assume we have a method to parse this JSON string into the required format
        Map<String, Object> predictedData = new HashMap<>();
        List<FreqPredictionDTO> list = new LinkedList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            predictedData = objectMapper.readValue(jsonStr, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (predictedData != null && predictedData.containsKey("choices") ){
            List<Map<String, Object>> choices = (List<Map<String, Object>>) predictedData.get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> content = (Map<String, Object>) firstChoice.get("message");
                try {
                   String freqPredictionDTOString = (String) content.get("content");
                    JSONArray jsonArray = new JSONArray(freqPredictionDTOString);
                    System.out.println(jsonArray);

                    // convert JSONArray to List<FreqPredictionDTO>
                    list = objectMapper.readValue(jsonArray.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, FreqPredictionDTO.class));

                }catch (Exception e){
                    System.out.println("Error parsing content: " + e.getMessage());
                }
            }
        }
        return list;
    }


    @Async
    @Override
    public APIResponse getPredication(Map<String, Map<String, List<DataDto>>> graphData) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant");
        requestBody.put("messages", List.of(
               // Map.of("role", "system", "content", "You are a helpful assistant that provides energy predictions based on solar and wind data."),
                Map.of("role", "user", "content", "Given this input, predict frequency dips, RoCoF, grid health and inertia need and provide the response in the given List of FreqPredictionDTO class public class FreqPredictionDTO { private String timestamp; private double predictedFreq; private double rocOfFreq; private String gridHealth; private boolean syntheticInertiaRequired; private boolean triggerControlCommand; } format without any additional text and also remove the given input from the response keep only resulted output: " + graphData)
        ));

        try {
            disableSSLVerification();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpEntity entity = new HttpEntity(requestBody, new HttpHeaders() {{
            set("Authorization", "Bearer " + apiKey);
            set("Content-Type", "application/json");
        }});
        ResponseEntity<String> predectedResponse = restTemplate.exchange(gorkUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {
        });
        return APIResponse.builder().status(200).data(processPredictedData(predectedResponse.getBody())).build();
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
