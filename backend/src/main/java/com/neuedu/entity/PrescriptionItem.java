package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("prescription_item")
public class PrescriptionItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long prescriptionId;
    private Long itemId;
    private Integer quantity;
    private String dosage;
    private BigDecimal amount;
    private Integer status; // 0-未收费 1-已收费 2-已退费
    private LocalDateTime createTime;
}