package com.neuedu.controller;

import com.neuedu.dto.PrescriptionItemDTO;
import com.neuedu.dto.PrescriptionVO;
import com.neuedu.dto.Result;
import com.neuedu.entity.Prescription;
import com.neuedu.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescription")
@RequiredArgsConstructor
@CrossOrigin
public class PrescriptionController {

    private final PrescriptionService presService;

    @PostMapping("/create")
    public Result<Prescription> create(@RequestParam Long registrationId) {
        return Result.success(presService.createPrescription(registrationId));
    }

    @PostMapping("/addItem")
    public Result<Void> addItem(@RequestParam Long prescriptionId, @RequestBody PrescriptionItemDTO dto) {
        presService.addItem(prescriptionId, dto);
        return Result.success();
    }

    @DeleteMapping("/removeItem/{itemId}")
    public Result<Void> removeItem(@PathVariable Long itemId) {
        presService.removeItem(itemId);
        return Result.success();
    }

    @PutMapping("/submit/{prescriptionId}")
    public Result<Prescription> submit(@PathVariable Long prescriptionId) {
        return Result.success(presService.submitPrescription(prescriptionId));
    }

    @GetMapping("/list/{regId}")
    public Result<List<PrescriptionVO>> list(@PathVariable Long regId) {
        return Result.success(presService.listByRegistration(regId));
    }

    @GetMapping("/detail/{prescriptionId}")
    public Result<List<PrescriptionVO>> detail(@PathVariable Long prescriptionId) {
        return Result.success(presService.getPrescriptionDetail(prescriptionId));
    }
}