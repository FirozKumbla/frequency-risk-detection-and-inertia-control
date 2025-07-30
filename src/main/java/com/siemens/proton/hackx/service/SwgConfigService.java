package com.siemens.proton.hackx.service;

import com.siemens.proton.hackx.model.SwitchgearDTO;
import com.siemens.proton.hackx.response.APIResponse;

public interface SwgConfigService {
    APIResponse createSwitchgear(SwitchgearDTO switchgearDTO);
    APIResponse getSwitchgearById(Integer id);
    APIResponse getAllSwitchgear();
    APIResponse updateSwgConfiguration(SwitchgearDTO switchgearDTO);
}
