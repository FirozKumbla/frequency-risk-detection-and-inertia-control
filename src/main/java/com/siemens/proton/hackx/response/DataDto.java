package com.siemens.proton.hackx.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
    private String time;
    private Double value;
    private Double voltage;
}
