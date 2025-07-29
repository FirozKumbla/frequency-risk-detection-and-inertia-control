package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.response.APIResponse;

public interface WeatherService {
    APIResponse getWeatherData(String latitude, String longitude);
}
