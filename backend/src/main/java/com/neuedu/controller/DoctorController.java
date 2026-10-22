package com.neuedu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.dto.Result;
import com.neuedu.entity.Doctor;
import com.neuedu.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@CrossOrigin
public class DoctorController {

    private final DoctorMapper doctorMapper;

    @GetMapping("/list")
    public Result<List<Doctor>> list(@RequestParam(required = false) Long deptId) {
        LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
        if (deptId != null) wrapper.eq(Doctor::getDeptId, deptId);
        return Result.success(doctorMapper.selectList(wrapper));
    }
}