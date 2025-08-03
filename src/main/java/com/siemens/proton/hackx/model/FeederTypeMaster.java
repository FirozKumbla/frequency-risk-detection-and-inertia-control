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
public class FeederTypeMaster {
    private int id;
    private String feederName;
    private String feederType;
    private int swgTypeId;

}
