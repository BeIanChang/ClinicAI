package com.clinicai.encounter.app;

public interface EventPublisher {
    void publish(String routingKey, Object payload);
}
