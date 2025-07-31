package com.siemens.proton.hackx.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siemens.proton.hackx.model.FreqPredictionDTO;
import com.siemens.proton.hackx.model.PowerWeatherForecastDto;
import com.siemens.proton.hackx.service.AiPredictionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AiPredictionServiceImpl implements AiPredictionService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String HF_TOKEN = "hf_your_token_here";
    private static final String HF_MODEL_URL = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.2";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<FreqPredictionDTO> predict(List<PowerWeatherForecastDto> input) {
        try {
            String prompt = "Given this input, predict frequency dips, RoCoF, grid health and inertia need:\n"
                    + objectMapper.writeValueAsString(input)
                    + "\nReturn JSON array like: [{\"timestamp\":\"2025-07-30\",\"predicted_freq_hz\":49.75,\"roc_of_freq\":0.8,\"grid_health\":\"Low\",\"synthetic_inertia_required\":true,\"trigger_control_command\":true}]";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(HF_TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            String body = objectMapper.writeValueAsString(Collections.singletonMap("inputs", prompt));

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    HF_MODEL_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String output = response.getBody();
            // Extract generated text from response JSON
            String generatedText = objectMapper.readTree(output).get(0).get("generated_text").asText();

            return objectMapper.readValue(
                    generatedText,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, FreqPredictionDTO.class)
            );

        } catch (Exception e) {
            log.error("HuggingFace AI call failed", e);
            return Collections.emptyList();
        }
    }

}
