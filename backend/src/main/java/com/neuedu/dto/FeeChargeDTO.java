package com.neuedu.dto;

import lombok.Data;

@Data
public class FeeChargeDTO {
    private String sourceType;   // prescription / application
    private Long detailId;       // 明细ID
    private String payMethod;
    private String operator;
}