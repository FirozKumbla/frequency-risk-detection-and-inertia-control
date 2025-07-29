package com.siemens.proton.hackx.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse {
    private int status;
    private String message;
    private Object data;
    private String errorCode;
    private String errorMessage;
}
