package com.neuedu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.dto.*;
import com.neuedu.entity.*;
import com.neuedu.mapper.*;
import com.neuedu.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final PatientMapper patientMapper;
    private final RegistrationMapper regMapper;
    private final PrescriptionMapper presMapper;
    private final PrescriptionItemMapper itemMapper;
    private final ApplicationMapper appMapper;
    private final FeeRecordMapper feeRecordMapper;
    private final ChargeItemMapper chargeItemMapper;

    // ============ 挂号 ============
    @Override
    @Transactional
    public Registration register(RegistrationDTO dto) {
        // ========== 1. 查找患者（优先按身份证号，其次按病历号） ==========
        Patient patient = null;
        if (isNotBlank(dto.getIdCard())) {
            patient = patientMapper.selectOne(
                    new LambdaQueryWrapper<Patient>().eq(Patient::getIdCard, dto.getIdCard().trim())
            );
        }
        if (patient == null && isNotBlank(dto.getPatientNo())) {
            patient = patientMapper.selectOne(
                    new LambdaQueryWrapper<Patient>().eq(Patient::getPatientNo, dto.getPatientNo().trim())
            );
        }

        // ========== 2. 若不存在则新建 ==========
        if (patient == null) {
            patient = new Patient();
            patient.setName(dto.getPatientName());
            patient.setGender(dto.getGender());
            patient.setBirthDate(dto.getBirthDate());
            // ⚠️ 关键：空字符串转 null
            patient.setIdCard(toNullIfBlank(dto.getIdCard()));
            patient.setPhone(toNullIfBlank(dto.getPhone()));
            patient.setAddress(toNullIfBlank(dto.getAddress()));

            // 病历号：P + 年月日 + 6位随机数
            String no = "P" + java.time.LocalDate.now().toString().replace("-", "")
                    + String.format("%06d", (int)(Math.random() * 1000000));
            patient.setPatientNo(no);
            patient.setCreateTime(LocalDateTime.now());
            patientMapper.insert(patient);
        } else {
            // 若已有患者，可以顺便更新一下信息（可选）
            boolean changed = false;
            if (isNotBlank(dto.getPhone()) && !dto.getPhone().equals(patient.getPhone())) {
                patient.setPhone(dto.getPhone());
                changed = true;
            }
            if (isNotBlank(dto.getAddress()) && !dto.getAddress().equals(patient.getAddress())) {
                patient.setAddress(dto.getAddress());
                changed = true;
            }
            if (changed) {
                patientMapper.updateById(patient);
            }
        }

        // ========== 3. 创建挂号记录 ==========
        Registration reg = new Registration();
        reg.setPatientId(patient.getId());
        reg.setVisitDate(dto.getVisitDate());
        reg.setNoon(dto.getNoon());
        reg.setDeptId(dto.getDeptId());
        reg.setDoctorId(dto.getDoctorId());
        reg.setRegLevel(dto.getRegLevel());
        reg.setRegFee(new BigDecimal("10"));
        reg.setStatus(0);
        reg.setCreateTime(LocalDateTime.now());
        regMapper.insert(reg);

        return reg;
    }

    // ========== 工具方法 ==========
    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String toNullIfBlank(String s) {
        return isNotBlank(s) ? s.trim() : null;
    }
    // ============ 退号 ============
    @Override
    @Transactional
    public void cancelRegistration(Long regId) {
        Registration reg = regMapper.selectById(regId);
        if (reg == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        if (reg.getStatus() == 2) {
            throw new RuntimeException("该挂号已退号");
        }

        // 检查处方是否有已收费明细
        List<Prescription> prescriptions = presMapper.selectList(
                new LambdaQueryWrapper<Prescription>().eq(Prescription::getRegistrationId, regId)
        );
        for (Prescription pres : prescriptions) {
            Long charged = itemMapper.selectCount(
                    new LambdaQueryWrapper<PrescriptionItem>()
                            .eq(PrescriptionItem::getPrescriptionId, pres.getId())
                            .eq(PrescriptionItem::getStatus, 1)
            );
            if (charged != null && charged > 0) {
                throw new RuntimeException("该挂号存在已收费项目，请先退费");
            }
        }

        // 检查申请单是否有已收费
        Long chargedApp = appMapper.selectCount(
                new LambdaQueryWrapper<Application>()
                        .eq(Application::getRegistrationId, regId)
                        .eq(Application::getStatus, 1)
        );
        if (chargedApp != null && chargedApp > 0) {
            throw new RuntimeException("该挂号存在已收费项目，请先退费");
        }

        reg.setStatus(2);
        regMapper.updateById(reg);
    }

    // ============ 看诊记录列表 ============
    @Override
    public Page<RegistrationVO> listRegistrations(Integer current, Integer size,
                                                  String patientNo, String patientName,
                                                  String startDate, String endDate) {
        Page<RegistrationVO> page = new Page<>(current, size);
        return regMapper.selectRegistrationList(page, patientNo, patientName, startDate, endDate);
    }

    // ============ 待收费/已收费明细 ============
    @Override
    public List<ChargeDetailVO> getChargeDetails(Long regId) {
        List<ChargeDetailVO> result = new ArrayList<>();

        List<Map<String, Object>> presItems = itemMapper.selectChargeItemsByRegId(regId);
        for (Map<String, Object> row : presItems) {
            ChargeDetailVO vo = new ChargeDetailVO();
            vo.setId(((Number) row.get("id")).longValue());
            vo.setSourceType("prescription");
            vo.setItemType("drug");
            vo.setItemName((String) row.get("item_name"));
            vo.setSpec((String) row.get("spec"));
            vo.setQuantity(((Number) row.get("quantity")).intValue());
            vo.setAmount((BigDecimal) row.get("amount"));
            vo.setStatus(((Number) row.get("status")).intValue());
            vo.setCreateTime(String.valueOf(row.get("create_time")));
            result.add(vo);
        }

        List<Map<String, Object>> apps = appMapper.selectChargeItemsByRegId(regId);
        for (Map<String, Object> row : apps) {
            ChargeDetailVO vo = new ChargeDetailVO();
            vo.setId(((Number) row.get("id")).longValue());
            vo.setSourceType("application");
            vo.setItemType("apply");
            vo.setItemName((String) row.get("item_name"));
            vo.setSpec((String) row.get("spec"));
            vo.setQuantity(1);
            vo.setAmount((BigDecimal) row.get("amount"));
            vo.setStatus(((Number) row.get("status")).intValue());
            vo.setCreateTime(String.valueOf(row.get("create_time")));
            result.add(vo);
        }

        return result;
    }

    // ============ 收费 ============
    @Override
    @Transactional
    public void chargeFee(FeeChargeDTO dto) {
        BigDecimal amount;
        Long registrationId;

        if ("prescription".equals(dto.getSourceType())) {
            PrescriptionItem item = itemMapper.selectById(dto.getDetailId());
            if (item == null || item.getStatus() != 0) {
                throw new RuntimeException("处方明细不存在或状态不可收费");
            }
            item.setStatus(1);
            itemMapper.updateById(item);
            amount = item.getAmount();
            Prescription pres = presMapper.selectById(item.getPrescriptionId());
            registrationId = pres.getRegistrationId();
        } else if ("application".equals(dto.getSourceType())) {
            Application app = appMapper.selectById(dto.getDetailId());
            if (app == null || app.getStatus() != 0) {
                throw new RuntimeException("申请单不存在或状态不可收费");
            }
            app.setStatus(1);
            appMapper.updateById(app);
            ChargeItem ci = chargeItemMapper.selectById(app.getItemId());
            amount = ci != null ? ci.getPrice() : BigDecimal.ZERO;
            registrationId = app.getRegistrationId();
        } else {
            throw new RuntimeException("未知的收费来源类型：" + dto.getSourceType());
        }

        FeeRecord record = new FeeRecord();
        record.setRegistrationId(registrationId);
        record.setDetailId(dto.getDetailId());
        record.setFeeType(1);
        record.setAmount(amount);
        record.setPayMethod(dto.getPayMethod());
        record.setOperator(dto.getOperator());
        record.setOperateTime(LocalDateTime.now());
        feeRecordMapper.insert(record);
    }

    // ============ 退费 ============
    @Override
    @Transactional
    public void refundFee(FeeRefundDTO dto) {
        BigDecimal amount;
        Long registrationId;

        if ("prescription".equals(dto.getSourceType())) {
            PrescriptionItem item = itemMapper.selectById(dto.getDetailId());
            if (item == null || item.getStatus() != 1) {
                throw new RuntimeException("处方明细未收费或已退费");
            }
            item.setStatus(2);
            itemMapper.updateById(item);
            amount = item.getAmount();
            Prescription pres = presMapper.selectById(item.getPrescriptionId());
            registrationId = pres.getRegistrationId();
        } else if ("application".equals(dto.getSourceType())) {
            Application app = appMapper.selectById(dto.getDetailId());
            if (app == null || app.getStatus() != 1) {
                throw new RuntimeException("申请单未收费或已退费");
            }
            app.setStatus(0);
            appMapper.updateById(app);
            ChargeItem ci = chargeItemMapper.selectById(app.getItemId());
            amount = ci != null ? ci.getPrice() : BigDecimal.ZERO;
            registrationId = app.getRegistrationId();
        } else {
            throw new RuntimeException("未知的退费来源类型");
        }

        FeeRecord record = new FeeRecord();
        record.setRegistrationId(registrationId);
        record.setDetailId(dto.getDetailId());
        record.setFeeType(2);
        record.setAmount(amount);
        record.setOperator(dto.getOperator());
        record.setOperateTime(LocalDateTime.now());
        feeRecordMapper.insert(record);
    }

    @Override
    public List<FeeRecord> getFeeRecords(Long regId) {
        return feeRecordMapper.selectList(
                new LambdaQueryWrapper<FeeRecord>()
                        .eq(FeeRecord::getRegistrationId, regId)
                        .orderByDesc(FeeRecord::getOperateTime)
        );
    }

    @Override
    public Registration getRegistrationById(Long regId) {
        return regMapper.selectById(regId);
    }
}