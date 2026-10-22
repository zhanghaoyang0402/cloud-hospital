package com.neuedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.dto.ApplicationVO;
import com.neuedu.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    @Select("SELECT a.id, a.registration_id, a.application_type, a.item_id, " +
            "c.item_name, c.price, c.spec, " +
            "a.exec_dept, a.urgency, a.purpose, a.status, a.result, a.apply_time, a.exec_time " +
            "FROM application a " +
            "LEFT JOIN charge_item c ON a.item_id = c.id " +
            "WHERE a.registration_id = #{regId} " +
            "ORDER BY a.apply_time DESC")
    List<ApplicationVO> selectVOByRegistrationId(@Param("regId") Long regId);

    /**
     * 查询某挂号下所有申请单（用于收费）
     */
    @Select("SELECT a.id, c.item_name, c.spec, 1 as quantity, c.price as amount, a.status, a.apply_time as create_time " +
            "FROM application a " +
            "LEFT JOIN charge_item c ON a.item_id = c.id " +
            "WHERE a.registration_id = #{regId} AND a.status IN (0,1,2)")
    List<java.util.Map<String, Object>> selectChargeItemsByRegId(@Param("regId") Long regId);
}