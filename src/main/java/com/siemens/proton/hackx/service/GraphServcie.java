package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.response.APIResponse;

public interface GraphServcie {
    APIResponse getGraphData(int locationId, int days);
}
