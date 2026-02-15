package com.example.gymapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ScanCodeRequest {

    @NotBlank(message = "code is required")
    @Size(max = 255, message = "code is too long")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
