package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("charge_item")
public class ChargeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String itemCode;
    private String itemName;
    private String itemType;
    private BigDecimal price;
    private String spec;
    private String unit;
}