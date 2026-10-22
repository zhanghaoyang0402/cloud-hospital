package com.neuedu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.neuedu.dto.ApplicationDTO;
import com.neuedu.dto.ApplicationVO;
import com.neuedu.entity.Application;
import com.neuedu.entity.Registration;
import com.neuedu.mapper.ApplicationMapper;
import com.neuedu.mapper.RegistrationMapper;
import com.neuedu.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationMapper appMapper;
    private final RegistrationMapper regMapper;

    @Override
    @Transactional
    public Application apply(ApplicationDTO dto) {
        Application app = new Application();
        BeanUtil.copyProperties(dto, app);
        app.setStatus(0);
        app.setApplyTime(LocalDateTime.now());
        appMapper.insert(app);

        // ========== 同步更新挂号状态 ==========
        Registration reg = regMapper.selectById(dto.getRegistrationId());
        if (reg != null && reg.getStatus() == 0) {
            reg.setStatus(1);
            regMapper.updateById(reg);
        }

        return app;
    }

    @Override
    public List<ApplicationVO> listByRegistration(Long regId) {
        return appMapper.selectVOByRegistrationId(regId);
    }

    @Override
    public void cancel(Long appId) {
        Application app = appMapper.selectById(appId);
        if (app == null || app.getStatus() != 0) {
            throw new RuntimeException("申请单不存在或状态不可取消");
        }
        app.setStatus(3);
        appMapper.updateById(app);
    }

    @Override
    public void execute(Long appId, String result) {
        Application app = appMapper.selectById(appId);
        if (app == null || app.getStatus() != 1) {
            throw new RuntimeException("申请单未收费或已执行");
        }
        app.setStatus(2);
        app.setResult(result);
        app.setExecTime(LocalDateTime.now());
        appMapper.updateById(app);
    }
}