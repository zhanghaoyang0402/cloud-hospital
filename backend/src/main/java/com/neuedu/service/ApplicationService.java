package com.neuedu.service;

import com.neuedu.dto.ApplicationDTO;
import com.neuedu.dto.ApplicationVO;
import com.neuedu.entity.Application;
import java.util.List;

public interface ApplicationService {
    Application apply(ApplicationDTO dto);
    List<ApplicationVO> listByRegistration(Long regId);
    void cancel(Long appId);
    void execute(Long appId, String result);
}