package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.response.APIResponse;

public interface FrequencyRiskService {
    APIResponse getWeatherData(String latitude, String longitude);

    APIResponse gridConfiguration(LocationConfigModel gridConfigRequest);

    APIResponse getGridConfiguration(Integer id);

    APIResponse getAllGridConfigurations();

    APIResponse updateGridConfiguration(LocationConfigModel gridConfigRequest);
}
