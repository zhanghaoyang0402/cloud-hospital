package com.neuedu.service;

import com.neuedu.dto.PrescriptionDTO;
import com.neuedu.dto.PrescriptionItemDTO;
import com.neuedu.dto.PrescriptionVO;
import com.neuedu.entity.Prescription;
import java.util.List;

public interface PrescriptionService {
    Prescription createPrescription(Long regId);
    void addItem(Long prescriptionId, PrescriptionItemDTO dto);
    void removeItem(Long itemId);
    Prescription submitPrescription(Long prescriptionId);
    List<PrescriptionVO> listByRegistration(Long regId);
    List<PrescriptionVO> getPrescriptionDetail(Long prescriptionId);
}