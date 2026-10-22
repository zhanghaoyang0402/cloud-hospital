package com.neuedu.dto;

import lombok.Data;

@Data
public class ApplicationDTO {
    private Long registrationId;
    private Integer applicationType;
    private Long itemId;
    private String execDept;
    private Integer urgency;
    private String purpose;
}