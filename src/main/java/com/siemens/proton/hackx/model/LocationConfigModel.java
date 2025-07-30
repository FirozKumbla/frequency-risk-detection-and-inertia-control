package com.siemens.proton.hackx.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_location_config")
public class LocationConfigModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "location_name")
    @NotEmpty
    private String locationName;

    @Column(name = "latitude")
    @NotEmpty
    private String latitude;

    @Column(name = "longitude")
    @NotEmpty
    private String longitude;

    @Column(name = "address")
    private String address;

    @Column(name = "wind_mill_count")
    private Integer windMillCount;

    @Column(name = "solar_panel_count")
    private Integer solarPanelCount;

}
