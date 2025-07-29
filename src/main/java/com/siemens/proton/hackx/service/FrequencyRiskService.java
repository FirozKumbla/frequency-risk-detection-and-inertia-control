package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.model.GridConfigModel;
import com.siemens.proton.hackx.response.APIResponse;

public interface FrequencyRiskService {
    APIResponse getWeatherData(String latitude, String longitude);

    APIResponse gridConfiguration(GridConfigModel gridConfigRequest);

    APIResponse getGridConfiguration(Integer id);

    APIResponse getAllGridConfigurations();
}
