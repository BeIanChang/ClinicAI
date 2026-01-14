package com.clinicai.lablist.app;

public interface EventPublisher {
    void publish(String routingKey, Object payload);
}
