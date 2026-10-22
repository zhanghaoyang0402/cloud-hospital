package com.neuedu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistrationVO {
    private Long id;               // 挂号ID
    private String patientName;    // 患者姓名
    private String patientNo;      // 病历号
    private String deptName;       // 科室名称
    private String doctorName;     // 医生姓名
    private LocalDate visitDate;   // 挂号日期
    private Integer status;        // 0-未就诊 1-已就诊 2-已退号

    private String chiefComplaint; // 主诉
}