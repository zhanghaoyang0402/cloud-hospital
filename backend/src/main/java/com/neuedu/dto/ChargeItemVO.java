package com.neuedu.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ChargeItemVO {
    private Long id;
    private String itemCode;
    private String itemName;
    private String itemType;   // drug/check/test/treatment
    private BigDecimal price;
    private String spec;
    private String unit;
}