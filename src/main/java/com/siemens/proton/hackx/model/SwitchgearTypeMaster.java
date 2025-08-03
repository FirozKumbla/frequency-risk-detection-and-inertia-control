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
public class SwitchgearTypeMaster {

    private int id;
    private String swgType;
    private String swgName;

}
