package com.clinicai.billing.adapters.payment;

import com.clinicai.billing.domain.model.Invoice;
import com.clinicai.billing.domain.port.PaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentGateway implements PaymentGatewayPort {
    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);

    @Override
    public boolean authorize(Invoice invoice, String method) {
        log.debug("Mock gateway authorizing invoice {} via method {}", invoice.getInvoiceId(), method);
        return true;
    }
}
