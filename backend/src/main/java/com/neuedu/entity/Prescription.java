package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("prescription")
public class Prescription {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long registrationId;
    private String prescriptionNo;
    private Integer status; // 0-草稿 1-已提交 2-已收费
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}