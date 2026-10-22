package com.neuedu.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {}
