package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.dto.MedicalRecordDTO;
import com.neuedu.entity.MedicalRecord;
import com.neuedu.entity.Registration;
import com.neuedu.mapper.MedicalRecordMapper;
import com.neuedu.mapper.RegistrationMapper;
import com.neuedu.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordMapper recordMapper;
    private final RegistrationMapper regMapper;   // ⚠️ 新增注入

    @Override
    @Transactional
    public MedicalRecord saveOrUpdate(MedicalRecordDTO dto) {
        MedicalRecord record = recordMapper.selectOne(
                new LambdaQueryWrapper<MedicalRecord>()
                        .eq(MedicalRecord::getRegistrationId, dto.getRegistrationId())
        );
        boolean isNew = false;
        if (record == null) {
            record = new MedicalRecord();
            isNew = true;
        }
        BeanUtils.copyProperties(dto, record);
        if (isNew) {
            record.setCreateTime(LocalDateTime.now());
            recordMapper.insert(record);
        } else {
            record.setUpdateTime(LocalDateTime.now());
            recordMapper.updateById(record);
        }

        // ========== 关键：更新挂号状态为「已就诊」 ==========
        markRegistrationVisited(dto.getRegistrationId());

        return record;
    }

    /**
     * 将挂号状态更新为「已就诊」(status=1)
     */
    private void markRegistrationVisited(Long regId) {
        Registration reg = regMapper.selectById(regId);
        if (reg != null && reg.getStatus() == 0) {  // 只有未就诊时才更新
            reg.setStatus(1);
            regMapper.updateById(reg);
        }
    }


    @Override
    public MedicalRecord getByRegistrationId(Long regId) {
        return recordMapper.selectOne(
                new LambdaQueryWrapper<MedicalRecord>().eq(MedicalRecord::getRegistrationId, regId)
        );
    }
}