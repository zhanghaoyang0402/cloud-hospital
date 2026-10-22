package com.neuedu.controller;

import com.neuedu.dto.Result;
import com.neuedu.entity.Department;
import com.neuedu.entity.Patient;
import com.neuedu.mapper.DepartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dept")
@RequiredArgsConstructor
@CrossOrigin
public class DepartmentController {
    private final DepartmentMapper deptMapper;
    @GetMapping("/list")
    public Result<List<Department>> list() {
        return Result.success(deptMapper.selectList(null));
    }
}