package com.neuedu.dto;

import lombok.Data;
import java.util.List;

@Data
public class PrescriptionDTO {
    private Long registrationId;
    private List<PrescriptionItemDTO> items;
}

