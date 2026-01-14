package com.clinicai.data.adapters.amqp;

import com.clinicai.data.app.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {
    private final RabbitTemplate tpl;
    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);

    public RabbitEventPublisher(RabbitTemplate tpl) {
        this.tpl = tpl;
    }

    @Override
    public void publish(String routingKey, Object payload) {
        log.info("[Data] publishing event key={} payload={}", routingKey, payload);
        tpl.convertAndSend(RabbitConfig.EXCHANGE, routingKey, payload);
    }
}
