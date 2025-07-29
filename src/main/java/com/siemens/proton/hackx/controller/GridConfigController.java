package com.siemens.proton.hackx.controller;

import com.siemens.proton.hackx.model.GridConfigModel;
import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.FrequencyRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class GridConfigController {

    @Autowired
    private FrequencyRiskService frequencyRiskService;

    @PostMapping("/grid/config")
    public ResponseEntity<APIResponse> createGridConfiguration(@RequestBody GridConfigModel gridConfigRequest) {
        // This method will handle the creation of a new grid configuration.
        APIResponse response = frequencyRiskService.gridConfiguration(gridConfigRequest);
        return ResponseEntity.status(response.getStatus())
                .body(response);
    }

    @PutMapping("/grid/config")
    public ResponseEntity<APIResponse> updateGridConfiguration(@RequestBody GridConfigModel gridConfigRequest) {
        // This method will handle the creation of a new grid configuration.
        APIResponse response = frequencyRiskService.updateGridConfiguration(gridConfigRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
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
