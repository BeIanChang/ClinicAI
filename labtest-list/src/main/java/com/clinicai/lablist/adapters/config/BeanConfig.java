package com.clinicai.lablist.adapters.config;

import com.clinicai.lablist.domain.port.LabListPort;
import com.clinicai.lablist.domain.service.LabListService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public LabListService labListService(LabListPort port) {
        return new LabListService(port);
    }
}
