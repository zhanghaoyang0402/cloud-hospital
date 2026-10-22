package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long registrationId;
    private Long detailId;
    private Integer feeType;   // 1-收费 2-退费
    private BigDecimal amount;
    private String payMethod;
    private LocalDateTime operateTime;
    private String operator;
}