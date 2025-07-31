package com.siemens.proton.hackx.service.impl;

import com.siemens.proton.hackx.model.SwitchgearDTO;
import com.siemens.proton.hackx.repository.SwgConfigRepository;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.SwgConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwgConfigServiceImpl implements SwgConfigService {

    @Autowired
    private SwgConfigRepository swgConfigRepository;

    @Override
    public APIResponse createSwitchgear(SwitchgearDTO switchgearDTO) {
        try {
            swgConfigRepository.save(switchgearDTO);
            return APIResponse.builder()
                    .status(201)
                    .data(switchgearDTO)
                    .message("Switchgear configuration saved successfully")
                    .build();
        } catch (Exception e) {
            return APIResponse.builder()
                    .status(500)
                    .message("Failed to switchgear grid configuration: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public APIResponse getSwitchgearById(Integer id) {
        SwitchgearDTO switchgearDTO = swgConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Switchgear configuration not found with id: " + id));
        return APIResponse.builder()
                .status(200)
                .message("Switchgear configuration retrieved successfully")
                .data(switchgearDTO)
                .build();
    }

    @Override
    public APIResponse getAllSwitchgear() {
        List<SwitchgearDTO> swgConfigs = swgConfigRepository.findAll();
        return APIResponse.builder()
                .status(200)
                .message("All grid configurations retrieved successfully")
                .data(swgConfigs)
                .build();
    }

    @Override
    public APIResponse getAllSwitchgearByLocation(Integer id) {
        List<SwitchgearDTO> swgConfigs = swgConfigRepository.findAll();
        List<SwitchgearDTO> swgConfigsByLoc = swgConfigs.stream()
                .filter(swg -> swg.getLocationId() != null && swg.getLocationId().equals(id))
                .toList();
        if(!swgConfigsByLoc.isEmpty()){
            return APIResponse.builder()
                    .status(200)
                    .message("All grid configurations for location " + id + " retrieved successfully")
                    .data(swgConfigsByLoc)
                    .build();
        } else {
            return APIResponse.builder()
                    .status(404)
                    .message("No switchgear configurations found for location with id: " + id)
                    .build();
        }
    }

    @Override
    public APIResponse updateSwgConfiguration(SwitchgearDTO switchgearDTO) {
        try {
            if (switchgearDTO.getSwgId() == null || !swgConfigRepository.existsById(switchgearDTO.getSwgId())) {
                return APIResponse.builder()
                        .status(404)
                        .message("Swg configuration not found with id: " + switchgearDTO.getSwgId())
                        .build();
            }
            switchgearDTO = swgConfigRepository.save(switchgearDTO);
            return APIResponse.builder()
                    .status(204)
                    .message("Swg configuration updated successfully")
                    .data(switchgearDTO)
                    .build();
        } catch (Exception e) {
            return APIResponse.builder()
                    .status(500)
                    .message("Failed to update swg configuration: " + e.getMessage())
                    .build();
        }
    }


}
