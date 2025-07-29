package com.siemens.proton.hackx.controller;

import com.siemens.proton.hackx.model.GridConfigModel;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class GridConfigController {

    @Autowired
    private FrequencyRiskService frequencyRiskService;

    @PostMapping("/grid/config")
    public ResponseEntity<APIResponse> gridConfiguration(@RequestBody GridConfigModel gridConfigRequest) {
        // This method will interact with the WeatherService to fetch weather data for the given location.
        // It will return a ResponseEntity containing the APIResponse with the weather data.
        return ResponseEntity.ok(frequencyRiskService.gridConfiguration(gridConfigRequest));
    }

    @GetMapping("/grid/config/{id}")
    public ResponseEntity<APIResponse> getGridConfiguration(@PathVariable Integer id) {
        // This method will fetch the grid configuration by ID.
        // It will return a ResponseEntity containing the APIResponse with the grid configuration data.
        return ResponseEntity.ok(frequencyRiskService.getGridConfiguration(id));
    }

    @GetMapping("/grid/all")
    public ResponseEntity<APIResponse> getAllGridConfigurations() {
        // This method will fetch all grid configurations.
        // It will return a ResponseEntity containing the APIResponse with the list of grid configurations.
        return ResponseEntity.ok(frequencyRiskService.getAllGridConfigurations());
    }

}
