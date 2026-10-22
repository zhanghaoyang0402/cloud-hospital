package com.neuedu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neuedu.dto.ChargeItemVO;
import com.neuedu.entity.ChargeItem;
import com.neuedu.mapper.ChargeItemMapper;
import com.neuedu.service.ChargeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChargeItemServiceImpl implements ChargeItemService {

    private final ChargeItemMapper chargeItemMapper;

    @Override
    public List<ChargeItemVO> listByType(String itemType) {
        LambdaQueryWrapper<ChargeItem> wrapper = new LambdaQueryWrapper<>();
        if (itemType != null && !itemType.isEmpty()) {
            wrapper.eq(ChargeItem::getItemType, itemType);
        }
        List<ChargeItem> list = chargeItemMapper.selectList(wrapper);
        return list.stream().map(item -> {
            ChargeItemVO vo = new ChargeItemVO();
            BeanUtil.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}