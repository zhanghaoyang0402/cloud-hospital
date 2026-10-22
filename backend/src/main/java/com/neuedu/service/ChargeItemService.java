package com.neuedu.service;

import com.neuedu.dto.ChargeItemVO;
import java.util.List;

public interface ChargeItemService {
    /**
     * 按类型查询项目
     * @param itemType drug/check/test/treatment，为 null 时查询全部
     */
    List<ChargeItemVO> listByType(String itemType);
}