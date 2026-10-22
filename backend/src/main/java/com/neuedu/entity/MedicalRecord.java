package com.neuedu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("medical_record")
public class MedicalRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long registrationId;
    private String chiefComplaint;
    private String presentIllness;
    private String pastHistory;
    private String allergyHistory;
    private String physicalExam;
    private String treatmentStatus;
    private String examAdvice;
    private String precautions;
    private String diagnosisIcd;
    private String diagnosisName;
    private LocalDate diagnosisDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}