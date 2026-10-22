package com.neuedu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ApplicationVO {
    private Long id;
    private Long registrationId;
    private Integer applicationType;   // 1-检查 2-检验 3-处置
    private Long itemId;
    private String itemName;           // 项目名称
    private BigDecimal price;          // 单价
    private String spec;               // 规格
    private String execDept;
    private Integer urgency;
    private String purpose;
    private Integer status;
    private String result;
    private LocalDateTime applyTime;
    private LocalDateTime execTime;
}