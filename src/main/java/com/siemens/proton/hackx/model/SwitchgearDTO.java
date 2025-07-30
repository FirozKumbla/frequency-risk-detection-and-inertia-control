package com.siemens.proton.hackx.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_swg_config")
public class SwitchgearDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer swgId;

    @Column(name = "swg_name")
    private String swgName;

    @Column(name = "incomer_feeder")
    private Integer incomerFeeder;

    @Column(name = "outgoing_feeder")
    private Integer outgoingFeeder;

    @Column(name = "active_incomer_feeder")
    private Integer activeIncomerFeeder;

    @Column(name = "active_outgoing_feeder")
    private Integer activeOutgoingFeeder;

    @Column(name = "location_id")
    private Integer locationId;

}
