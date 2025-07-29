package com.siemens.proton.hackx.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_grid_config")
public class GridConfigModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "grid_name")
    @NotEmpty
    private String gridName;

    @Column(name = "latitude")
    @NotEmpty
    private String latitude;

    @Column(name = "longitude")
    @NotEmpty
    private String longitude;

    @Column(name = "grid_type")
    private String gridType;

    @Column(name = "wind_mill_count")
    private Integer windMillCount;

    @Column(name = "solar_panel_count")
    private Integer solarPanelCount;

    @Column(name = "grid_capacity")
    private Double gridCapacity; // in MW (MegaWatts)

    @Column(name = "grid_status")
    private String gridStatus; // e.g., "active", "inactive", "maintenance"

}
