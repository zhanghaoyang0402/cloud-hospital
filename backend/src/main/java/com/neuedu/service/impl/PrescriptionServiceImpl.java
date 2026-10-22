package com.neuedu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.dto.PrescriptionItemDTO;
import com.neuedu.dto.PrescriptionItemVO;
import com.neuedu.dto.PrescriptionVO;
import com.neuedu.entity.ChargeItem;
import com.neuedu.entity.Prescription;
import com.neuedu.entity.PrescriptionItem;
import com.neuedu.entity.Registration;
import com.neuedu.mapper.ChargeItemMapper;
import com.neuedu.mapper.PrescriptionItemMapper;
import com.neuedu.mapper.PrescriptionMapper;
import com.neuedu.mapper.RegistrationMapper;
import com.neuedu.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionMapper presMapper;
    private final PrescriptionItemMapper itemMapper;
    private final ChargeItemMapper chargeItemMapper;
    private final RegistrationMapper regMapper;

    @Override
    public Prescription createPrescription(Long regId) {
        Prescription p = new Prescription();
        p.setRegistrationId(regId);
        p.setPrescriptionNo("P" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6));
        p.setStatus(0);
        p.setCreateTime(LocalDateTime.now());
        presMapper.insert(p);
        return p;
    }

    @Override
    public void addItem(Long prescriptionId, PrescriptionItemDTO dto) {
        Prescription pres = presMapper.selectById(prescriptionId);
        if (pres == null || pres.getStatus() != 0) {
            throw new RuntimeException("处方不存在或已提交，不可编辑");
        }
        ChargeItem item = chargeItemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new RuntimeException("项目不存在");
        }
        PrescriptionItem pi = new PrescriptionItem();
        pi.setPrescriptionId(prescriptionId);
        pi.setItemId(dto.getItemId());
        pi.setQuantity(dto.getQuantity());
        pi.setDosage(dto.getDosage());
        pi.setAmount(item.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        pi.setStatus(0);
        pi.setCreateTime(LocalDateTime.now());
        itemMapper.insert(pi);
    }

    @Override
    public void removeItem(Long itemId) {
        PrescriptionItem pi = itemMapper.selectById(itemId);
        if (pi == null || pi.getStatus() != 0) {
            throw new RuntimeException("明细不存在或已收费，不可删除");
        }
        itemMapper.deleteById(itemId);
    }

    @Override
    @Transactional
    public Prescription submitPrescription(Long prescriptionId) {
        Prescription pres = presMapper.selectById(prescriptionId);
        if (pres == null || pres.getStatus() != 0) {
            throw new RuntimeException("处方不存在或已提交");
        }
        Long count = itemMapper.selectCount(
                new LambdaQueryWrapper<PrescriptionItem>()
                        .eq(PrescriptionItem::getPrescriptionId, prescriptionId)
        );
        if (count == 0) {
            throw new RuntimeException("处方为空，无法提交");
        }
        pres.setStatus(1);
        presMapper.updateById(pres);

        // ========== 关键：同步更新挂号状态 ==========
        Registration reg = regMapper.selectById(pres.getRegistrationId());
        if (reg != null && reg.getStatus() == 0) {
            reg.setStatus(1);
            regMapper.updateById(reg);
        }

        return pres;
    }
    @Override
    public List<PrescriptionVO> listByRegistration(Long regId) {
        List<Prescription> presList = presMapper.selectList(
                new LambdaQueryWrapper<Prescription>()
                        .eq(Prescription::getRegistrationId, regId)
                        .orderByDesc(Prescription::getCreateTime)
        );
        List<PrescriptionVO> voList = new ArrayList<>();
        for (Prescription p : presList) {
            PrescriptionVO vo = new PrescriptionVO();
            BeanUtil.copyProperties(p, vo);
            List<PrescriptionItemVO> items = itemMapper.selectVOByPrescriptionId(p.getId());
            vo.setItems(items);
            vo.setTotalAmount(calcTotal(items));
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<PrescriptionVO> getPrescriptionDetail(Long prescriptionId) {
        Prescription p = presMapper.selectById(prescriptionId);
        if (p == null) {
            throw new RuntimeException("处方不存在");
        }
        PrescriptionVO vo = new PrescriptionVO();
        BeanUtil.copyProperties(p, vo);
        List<PrescriptionItemVO> items = itemMapper.selectVOByPrescriptionId(prescriptionId);
        vo.setItems(items);
        vo.setTotalAmount(calcTotal(items));
        return List.of(vo);
    }

    /**
     * 计算处方明细总金额（避免使用可能触发 IDEA 报错的方法引用写法）
     */
    private BigDecimal calcTotal(List<PrescriptionItemVO> items) {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (PrescriptionItemVO item : items) {
            if (item != null && item.getAmount() != null) {
                total = total.add(item.getAmount());
            }
        }
        return total;
    }
}