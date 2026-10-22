package com.neuedu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.dto.*;
import com.neuedu.entity.FeeRecord;
import com.neuedu.entity.Registration;
import com.neuedu.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/registration")
@RequiredArgsConstructor
@CrossOrigin
public class RegistrationController {

    private final RegistrationService regService;

    @PostMapping("/register")
    public Result<Registration> register(@RequestBody RegistrationDTO dto) {
        return Result.success(regService.register(dto));
    }

    // ---------- 退号：使用 PUT ----------
    @PutMapping("/cancel/{regId}")
    public Result<Void> cancel(@PathVariable Long regId) {
        regService.cancelRegistration(regId);
        return Result.success();
    }

    // ---------- 看诊记录列表：参数全部用 String 接收，避免空串转换失败 ----------
    @GetMapping("/list")
    public Result<Page<RegistrationVO>> list(
            @RequestParam(required = false) String patientNo,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<RegistrationVO> page = regService.listRegistrations(
                current, size,
                trimToNull(patientNo),
                trimToNull(patientName),
                trimToNull(startDate),
                trimToNull(endDate)
        );
        return Result.success(page);
    }

    /**
     * 将空串转换为 null，避免 MyBatis 的 <if> 判断异常
     */
    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @GetMapping("/{regId}")
    public Result<Registration> getReg(@PathVariable Long regId) {
        return Result.success(regService.getRegistrationById(regId));
    }

    // ---------- 收费相关 ----------
    @GetMapping("/chargeDetails/{regId}")
    public Result<List<ChargeDetailVO>> chargeDetails(@PathVariable Long regId) {
        return Result.success(regService.getChargeDetails(regId));
    }

    @PostMapping("/charge")
    public Result<Void> charge(@RequestBody FeeChargeDTO dto) {
        regService.chargeFee(dto);
        return Result.success();
    }

    @PostMapping("/refund")
    public Result<Void> refund(@RequestBody FeeRefundDTO dto) {
        regService.refundFee(dto);
        return Result.success();
    }

    @GetMapping("/feeRecords/{regId}")
    public Result<List<FeeRecord>> feeRecords(@PathVariable Long regId) {
        List<FeeRecord> list = regService.getFeeRecords(regId);
        return Result.success(list == null ? new ArrayList<>() : list);
    }
}