package com.email.email.ports;

import java.util.Map;

public interface EmailServicePort {
    void processEmail(Map<String, Object> emailPayload);
}
