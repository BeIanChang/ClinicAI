package com.clinicai.data.adapters.config;

import com.clinicai.data.domain.port.ReportPort;
import com.clinicai.data.domain.service.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ReportService reportService(ReportPort reports) {
        return new ReportService(reports);
    }
}
