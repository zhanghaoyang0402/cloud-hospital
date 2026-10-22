package com.neuedu.dto;

import lombok.Data;
import java.util.List;
@Data
public class PrescriptionItemDTO {
    private Long itemId;
    private Integer quantity;
    private String dosage;
}