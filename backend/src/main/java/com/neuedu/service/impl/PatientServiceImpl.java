package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.entity.Patient;
import com.neuedu.mapper.PatientMapper;
import com.neuedu.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientMapper patientMapper;
    @Override
    public List<Patient> searchPatients(String keyword) {
        return patientMapper.selectList(
                new LambdaQueryWrapper<Patient>()
                        .like(Patient::getName, keyword)
                        .or()
                        .like(Patient::getPatientNo, keyword)
        );
    }
}