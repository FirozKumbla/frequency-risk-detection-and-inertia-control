package com.siemens.proton.hackx.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Entity
//@Table(name = "tbl_wind_mill_config_m")
public class WindMillConfigModel {


// Rotor Diameter
    private Double airDensity; // in kg/m^3
    private Double windSpeed; // in m/s

    // Power Curve
    private Double rotorDiameter; // in meters
    private Double cutInSpeed; // in m/s
    private Double ratedSpeed; // in m/s
    private Double cutOutSpeed; // in m/s

    // Power Output
    private Double ratedPowerOutput; // in MW (MegaWatts)
    private Double maxPowerOutput; // in MW (MegaWatts)

    // Operational Parameters
    private String operationalStatus; // e.g., "operational", "maintenance", "fault"
}
