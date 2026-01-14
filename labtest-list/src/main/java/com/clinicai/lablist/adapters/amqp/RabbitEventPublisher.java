package com.clinicai.lablist.adapters.amqp;

import com.clinicai.lablist.app.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {
    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    private final RabbitTemplate tpl;

    public RabbitEventPublisher(RabbitTemplate tpl) {
        this.tpl = tpl;
    }

    @Override
    public void publish(String routingKey, Object payload) {
        log.info("[LabTest-List] publishing event key={} payload={}", routingKey, payload);
        tpl.convertAndSend(RabbitConfig.EXCHANGE, routingKey, payload);
    }
}
