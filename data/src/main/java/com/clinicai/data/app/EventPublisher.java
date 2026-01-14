package com.clinicai.data.app;

public interface EventPublisher {
    void publish(String routingKey, Object payload);
}
