package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.response.DataDto;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

public interface GraphServcie {
    APIResponse getGraphData(int locationId, int days);

    @Async
    APIResponse getPredication(Map<String, Map<String, List<DataDto>>> graphData);
}
