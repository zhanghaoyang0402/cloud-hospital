package com.neuedu.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicalRecordDTO {
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
}