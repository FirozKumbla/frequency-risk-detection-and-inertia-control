package com.siemens.proton.hackx.controller;

import com.siemens.proton.hackx.model.FeederTypeMaster;
import com.siemens.proton.hackx.model.SwitchgearDTO;
import com.siemens.proton.hackx.model.SwitchgearTypeMaster;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.SwgConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class SwgConfigController {

    @Autowired
    private SwgConfigService swgConfigService;

    @PostMapping("/swg/config")
    public ResponseEntity<APIResponse> createSwgConfiguration(@RequestBody SwitchgearDTO switchgearDTO) {
        // This method will handle the creation of a new swg configuration.
        APIResponse response = swgConfigService.createSwitchgear(switchgearDTO);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    @PutMapping("/swg/config")
    public ResponseEntity<APIResponse> updateGridConfiguration(@RequestBody SwitchgearDTO switchgearDTO) {
        // This method will handle the update of a new swg configuration.
        APIResponse response = swgConfigService.updateSwgConfiguration(switchgearDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/swg/config/{id}")
    public ResponseEntity<APIResponse> getGridConfiguration(@PathVariable Integer id) {
        // This method will fetch the swg configuration by ID.
        return ResponseEntity.ok(swgConfigService.getSwitchgearById(id));
    }


    @GetMapping("/swg/all")
    public ResponseEntity<APIResponse> getAllGridConfigurations() {
        // This method will fetch all swg configurations.
        return ResponseEntity.ok(swgConfigService.getAllSwitchgear());
    }

    @GetMapping("/swg/loc/{id}")
    public ResponseEntity<APIResponse> getAllGridConfigurations(@PathVariable Integer id) {
        // This method will fetch all swg configurations based on location ID.
        return ResponseEntity.ok(swgConfigService.getAllSwitchgearByLocation(id));
    }

    @GetMapping("/swg/type")
    public ResponseEntity<APIResponse> getSwgTypeConfigurations() {
        return ResponseEntity.ok(APIResponse.builder().data(SwitchgearTypeMaster.builder().id(1).swgType("NXAIR (up to 17.5kV 40kA)").swgName("NXAIR").build()).status(200).message("Success").build());
    }

    @GetMapping("/feeder/type")
    public ResponseEntity<APIResponse> getFeederTypeConfigurations() {
        // This method will fetch all swg configurations.
        return ResponseEntity.ok(APIResponse.builder().data(FeederTypeMaster.builder().id(1).feederName("Contactor").feederType("Incomer/Outgoing").swgTypeId(1).build()).status(200).message("Success").build());
    }

    @GetMapping("/default/value")
    public ResponseEntity<APIResponse> getDefaultValues() {
        // This method will fetch default values for swg configurations.
        APIResponse aPIResponse =   APIResponse.builder().status(200).data(Map.of("Solar_Panel_Type", Map.of("RATED_POWER_PER_PANEL", "300.0 W", "TEMP_DERATE_PER_C", 0.005, "MAX_UV_INDEX", 12.0),
                "Wind_Mill_Type", Map.of("ratedPowerKW", 2000, "cutIn", 3.5, "rated", 13.0, "cutOut", 25.0))).build();
        return ResponseEntity.ok(aPIResponse);
    }
}
