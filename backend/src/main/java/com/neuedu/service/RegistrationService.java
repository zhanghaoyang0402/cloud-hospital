package com.neuedu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.dto.*;
import com.neuedu.entity.FeeRecord;
import com.neuedu.entity.Registration;

import java.util.List;

public interface RegistrationService {
    Registration register(RegistrationDTO dto);
    void cancelRegistration(Long regId);
    Page<RegistrationVO> listRegistrations(Integer current, Integer size,
                                           String patientNo, String patientName,
                                           String startDate, String endDate);
    List<ChargeDetailVO> getChargeDetails(Long regId);
    void chargeFee(FeeChargeDTO dto);
    void refundFee(FeeRefundDTO dto);
    List<FeeRecord> getFeeRecords(Long regId);
    Registration getRegistrationById(Long regId);
}