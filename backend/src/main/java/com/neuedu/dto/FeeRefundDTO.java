package com.neuedu.dto;

import lombok.Data;

@Data
public class FeeRefundDTO {
    private String sourceType;
    private Long detailId;
    private String operator;
}