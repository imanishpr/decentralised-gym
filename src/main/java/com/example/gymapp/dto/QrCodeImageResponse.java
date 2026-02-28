package com.example.gymapp.dto;

public class QrCodeImageResponse {

    private Long gymId;
    private String gymName;
    private String code;
    private String qrPngBase64;

    public Long getGymId() {
        return gymId;
    }

    public void setGymId(Long gymId) {
        this.gymId = gymId;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQrPngBase64() {
        return qrPngBase64;
    }

    public void setQrPngBase64(String qrPngBase64) {
        this.qrPngBase64 = qrPngBase64;
    }
}
