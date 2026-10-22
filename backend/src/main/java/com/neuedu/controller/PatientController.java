package com.neuedu.controller;

import com.neuedu.dto.Result;
import com.neuedu.entity.Patient;
import com.neuedu.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@CrossOrigin
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/search")
    public Result<List<Patient>> search(@RequestParam String keyword) {
        return Result.success(patientService.searchPatients(keyword));
    }
}