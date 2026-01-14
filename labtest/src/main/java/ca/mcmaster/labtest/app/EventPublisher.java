package ca.mcmaster.labtest.app;

public interface EventPublisher {
    void publish(String routingKey, Object payload);
}
