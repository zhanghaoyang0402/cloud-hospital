package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private LocalDate visitDate;
    private String noon;       // 1-上午 2-下午
    private Long deptId;
    private Long doctorId;
    private String regLevel;
    private BigDecimal regFee;
    private Integer status;    // 0-未就诊 1-已就诊 2-已退号
    private LocalDateTime createTime;
}