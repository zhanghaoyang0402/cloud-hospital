package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String patientNo;

    @TableField(insertStrategy = FieldStrategy.ALWAYS)  // 强制插入，即使为 null 也会插入（但需要数据库有默认值）
    private String name;

    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private String phone;
    private String address;
    private LocalDateTime createTime;
}