package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.model.GridPowerDTO;

public interface GridPowerService {
    GridPowerDTO getIncomingPowerToGrid(String latitude, String longitude);
}
