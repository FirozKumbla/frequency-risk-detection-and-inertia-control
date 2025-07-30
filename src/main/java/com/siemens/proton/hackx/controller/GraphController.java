package com.siemens.proton.hackx.controller;


import com.siemens.proton.hackx.response.APIResponse;
import com.siemens.proton.hackx.service.GraphServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     public ResponseEntity<APIResponse> getGraphData(String latitude, String longitude) {
         return ResponseEntity.ok(graphService.getGraphData(latitude, longitude));
     }


}
