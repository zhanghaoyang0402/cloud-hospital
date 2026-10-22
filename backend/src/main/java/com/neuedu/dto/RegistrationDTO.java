package com.neuedu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RegistrationDTO {
    private String patientNo;     // 病历号（若已存在）
    private String patientName;
    private String gender;
    private LocalDate birthDate;
    private String idCard;
    private String phone;
    private String address;
    private Long deptId;
    private Long doctorId;
    private String regLevel;
    private LocalDate visitDate;
    private String noon;
}