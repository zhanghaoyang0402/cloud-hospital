package com.neuedu.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionVO {
    private Long id;
    private Long registrationId;
    private String prescriptionNo;
    private Integer status;
    private BigDecimal totalAmount;    // 总金额
    private LocalDateTime createTime;
    private List<PrescriptionItemVO> items;
}

