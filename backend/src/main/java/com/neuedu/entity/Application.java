package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("application")
public class Application {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long registrationId;
    private Integer applicationType; // 1-检查 2-检验 3-处置
    private Long itemId;
    private String execDept;
    private Integer urgency;
    private String purpose;
    private Integer status; // 0-未收费 1-已收费 2-已执行 3-已取消
    private String result;
    private LocalDateTime applyTime;
    private LocalDateTime execTime;
}