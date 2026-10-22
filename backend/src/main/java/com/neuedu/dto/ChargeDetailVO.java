package com.neuedu.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ChargeDetailVO {
    private Long id;              // 明细ID
    private String sourceType;    // prescription / application
    private String itemType;      // drug / check / test / treatment
    private String itemName;      // 项目名称
    private String spec;          // 规格
    private Integer quantity;     // 数量
    private BigDecimal amount;    // 金额
    private Integer status;       // 0-未收费 1-已收费 2-已退费
    private String createTime;    // 开立时间（字符串方便前端展示）
}