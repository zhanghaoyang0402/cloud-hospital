package com.neuedu.service;

import com.neuedu.entity.Patient;

import java.util.List;

public interface PatientService {
    List<Patient> searchPatients(String keyword);
}