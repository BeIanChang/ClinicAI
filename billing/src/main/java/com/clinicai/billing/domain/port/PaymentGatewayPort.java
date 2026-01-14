package com.clinicai.billing.domain.port;

import com.clinicai.billing.domain.model.Invoice;

public interface PaymentGatewayPort {
    boolean authorize(Invoice invoice, String method);
}
