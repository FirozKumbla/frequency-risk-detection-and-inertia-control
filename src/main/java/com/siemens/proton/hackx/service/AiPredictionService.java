package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.model.FreqPredictionDTO;
import com.siemens.proton.hackx.model.PowerWeatherForecastDto;

import java.util.List;

public interface AiPredictionService {
    List<FreqPredictionDTO> predict(List<PowerWeatherForecastDto> inputList);
}
