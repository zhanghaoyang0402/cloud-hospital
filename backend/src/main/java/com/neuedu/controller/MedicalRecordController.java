package com.neuedu.controller;

import com.neuedu.dto.MedicalRecordDTO;
import com.neuedu.dto.Result;
import com.neuedu.entity.MedicalRecord;
import com.neuedu.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicalRecord")
@RequiredArgsConstructor
@CrossOrigin
public class MedicalRecordController {
    private final MedicalRecordService recordService;

    @PostMapping("/save")
    public Result<MedicalRecord> save(@RequestBody MedicalRecordDTO dto) {
        return Result.success(recordService.saveOrUpdate(dto));
    }

    @GetMapping("/get/{regId}")
    public Result<MedicalRecord> get(@PathVariable Long regId) {
        return Result.success(recordService.getByRegistrationId(regId));
    }
}