package com.siemens.proton.hackx.controller;

import com.siemens.proton.hackx.model.LocationConfigModel;
import com.siemens.proton.hackx.model.GridPowerDTO;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import com.siemens.proton.hackx.service.GridPowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class LocationConfigController {

    @Autowired
    private FrequencyRiskService frequencyRiskService;
    @Autowired
    private GridPowerService gridPowerService;

    @PostMapping("/location/config")
    public ResponseEntity<APIResponse> createGridConfiguration(@RequestBody LocationConfigModel locationConfigModel) {
        // This method will handle the creation of a new grid configuration.
        APIResponse response = frequencyRiskService.gridConfiguration(locationConfigModel);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    @PutMapping("/location/config")
    public ResponseEntity<APIResponse> updateGridConfiguration(@RequestBody LocationConfigModel gridConfigRequest) {
        // This method will handle the creation of a new grid configuration.
        APIResponse response = frequencyRiskService.updateGridConfiguration(gridConfigRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/location/config/{id}")
    public ResponseEntity<APIResponse> getGridConfiguration(@PathVariable Integer id) {
        // This method will fetch the grid configuration by ID.
        // It will return a ResponseEntity containing the APIResponse with the grid configuration data.
        return ResponseEntity.ok(frequencyRiskService.getGridConfiguration(id));
    }

    @GetMapping("/location/all")
    public ResponseEntity<APIResponse> getAllGridConfigurations() {
        // This method will fetch all grid configurations.
        // It will return a ResponseEntity containing the APIResponse with the list of grid configurations.
        return ResponseEntity.ok(frequencyRiskService.getAllGridConfigurations());
    }

    @GetMapping("/grid/power/lat/{latitude}/long/{longitude}")
    public ResponseEntity<GridPowerDTO> getGridPowerAndFrequency(@PathVariable("latitude") String latitude,
                                                     @PathVariable("longitude") String longitude) {
        // This method will fetch incoming power to the grid
        return ResponseEntity.ok(gridPowerService.getIncomingPowerToGrid(latitude, longitude));
    }


}
