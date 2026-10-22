package com.neuedu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.dto.RegistrationVO;
import com.neuedu.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {

    @Select("<script>" +
            "SELECT r.id, p.name AS patient_name, p.patient_no, " +
            "       d.dept_name, doc.name AS doctor_name, " +
            "       r.visit_date, r.status, m.chief_complaint " +
            "FROM registration r " +
            "LEFT JOIN patient p ON r.patient_id = p.id " +
            "LEFT JOIN department d ON r.dept_id = d.id " +
            "LEFT JOIN doctor doc ON r.doctor_id = doc.id " +
            "LEFT JOIN medical_record m ON r.id = m.registration_id " +
            "WHERE 1=1 " +
            "<if test='patientNo != null and patientNo != \"\"'> AND p.patient_no LIKE CONCAT('%', #{patientNo}, '%') </if>" +
            "<if test='patientName != null and patientName != \"\"'> AND p.name LIKE CONCAT('%', #{patientName}, '%') </if>" +
            "<if test='startDate != null and startDate != \"\"'> AND r.visit_date &gt;= #{startDate} </if>" +
            "<if test='endDate != null and endDate != \"\"'> AND r.visit_date &lt;= #{endDate} </if>" +
            "ORDER BY r.id DESC" +
            "</script>")
    Page<RegistrationVO> selectRegistrationList(Page<?> page,
                                                @Param("patientNo") String patientNo,
                                                @Param("patientName") String patientName,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate);
}