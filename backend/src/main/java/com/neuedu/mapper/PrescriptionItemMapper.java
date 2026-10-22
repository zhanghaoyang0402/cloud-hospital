package com.neuedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.dto.PrescriptionItemVO;
import com.neuedu.entity.PrescriptionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PrescriptionItemMapper extends BaseMapper<PrescriptionItem> {

    @Select("SELECT pi.id, c.item_name, c.spec, c.price, " +
            "pi.quantity, pi.dosage, pi.amount, pi.status " +
            "FROM prescription_item pi " +
            "LEFT JOIN charge_item c ON pi.item_id = c.id " +
            "WHERE pi.prescription_id = #{prescriptionId}")
    List<PrescriptionItemVO> selectVOByPrescriptionId(@Param("prescriptionId") Long prescriptionId);

    /**
     * 查询某挂号下所有处方明细（用于收费）
     */
    @Select("SELECT pi.id, c.item_name, c.spec, pi.quantity, pi.amount, pi.status, pi.create_time " +
            "FROM prescription_item pi " +
            "INNER JOIN prescription p ON pi.prescription_id = p.id " +
            "LEFT JOIN charge_item c ON pi.item_id = c.id " +
            "WHERE p.registration_id = #{regId} AND p.status = 1")
    List<java.util.Map<String, Object>> selectChargeItemsByRegId(@Param("regId") Long regId);
}