package com.siemens.proton.hackx.controller;


import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.response.DataDto;
import com.siemens.proton.hackx.service.GraphServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1")
public class GraphController {


    @Autowired
    private GraphServcie graphService;


    // This class will handle HTTP requests related to graph data.
    // It will use the GraphService to fetch and return graph information.
    // Additional methods for handling specific endpoints can be added here.

    // Example method to fetch graph data
    @GetMapping("/graph")
    public ResponseEntity<APIResponse> getGraphData(int locationId, int days) {
        return ResponseEntity.ok(graphService.getGraphData(locationId, days));
    }


    @PostMapping("/prediction")
    public ResponseEntity<APIResponse> getPrediction(@RequestBody Map<String, Map<String, List<DataDto>>> graphData) {
        return ResponseEntity.ok(graphService.getPredication(graphData));
    }


}
