package com.neuedu.controller;

import com.neuedu.dto.ChargeItemVO;
import com.neuedu.dto.Result;
import com.neuedu.service.ChargeItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chargeItem")
@RequiredArgsConstructor
@CrossOrigin
public class ChargeItemController {

    private final ChargeItemService chargeItemService;

    /**
     * 按类型查询项目
     * @param itemType drug/check/test/treatment，不传则查全部
     */
    @GetMapping("/list")
    public Result<List<ChargeItemVO>> list(@RequestParam(required = false) String itemType) {
        return Result.success(chargeItemService.listByType(itemType));
    }
}