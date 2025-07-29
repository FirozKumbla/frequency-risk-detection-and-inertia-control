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

}
