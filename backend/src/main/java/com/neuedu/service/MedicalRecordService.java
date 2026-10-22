package com.neuedu.service;

import com.neuedu.dto.MedicalRecordDTO;
import com.neuedu.entity.MedicalRecord;

public interface MedicalRecordService {
    MedicalRecord saveOrUpdate(MedicalRecordDTO dto);
    MedicalRecord getByRegistrationId(Long regId);
}

