package com.neuedu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PrescriptionItemVO {
    private Long id;
    private Long itemId;
    private String itemName;           // 药品名称
    private String spec;               // 规格
    private BigDecimal price;          // 单价
    private Integer quantity;
    private String dosage;             // 用法用量
    private BigDecimal amount;         // 金额
    private Integer status;
}