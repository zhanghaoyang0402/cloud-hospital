package com.neuedu.controller;

import com.neuedu.dto.ApplicationDTO;
import com.neuedu.dto.ApplicationVO;
import com.neuedu.dto.Result;
import com.neuedu.entity.Application;
import com.neuedu.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
@CrossOrigin
public class ApplicationController {
    private final ApplicationService appService;

    @PostMapping("/apply")
    public Result<Application> apply(@RequestBody ApplicationDTO dto) {
        return Result.success(appService.apply(dto));
    }

    @GetMapping("/list/{regId}")
    public Result<List<ApplicationVO>> list(@PathVariable Long regId) {
        return Result.success(appService.listByRegistration(regId));
    }

    @PutMapping("/cancel/{appId}")
    public Result<Void> cancel(@PathVariable Long appId) {
        appService.cancel(appId);
        return Result.success();
    }

    @PutMapping("/execute/{appId}")
    public Result<Void> execute(@PathVariable Long appId, @RequestParam String result) {
        appService.execute(appId, result);
        return Result.success();
    }
}