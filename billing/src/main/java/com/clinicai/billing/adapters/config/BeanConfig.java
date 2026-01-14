package com.clinicai.billing.adapters.config;

import com.clinicai.billing.app.Clock;
import com.clinicai.billing.app.EventPublisher;
import com.clinicai.billing.app.IdGenerator;
import com.clinicai.billing.domain.port.InvoicePort;
import com.clinicai.billing.domain.port.InvoiceStorePort;
import com.clinicai.billing.domain.port.PaymentGatewayPort;
import com.clinicai.billing.domain.port.PaymentPort;
import com.clinicai.billing.domain.service.InvoiceService;
import com.clinicai.billing.domain.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.UUID;

@Configuration
public class BeanConfig {

    @Bean public Clock clock() { return Instant::now; }
    @Bean public IdGenerator ids() { return () -> UUID.randomUUID().toString(); }

    @Bean
    public InvoicePort invoicePort(InvoiceStorePort invoices, EventPublisher events, IdGenerator ids, Clock clock) {
        return new InvoiceService(invoices, events, ids, clock);
    }

    @Bean
    public PaymentPort paymentPort(InvoiceStorePort invoices, EventPublisher events, Clock clock,
                                   PaymentGatewayPort paymentGateway) {
        return new PaymentService(invoices, events, clock, paymentGateway);
    }
}
