// RabbitEventPublisher.java
package com.clinicai.encounter.adapters.amqp;

import com.clinicai.encounter.app.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {
    private final RabbitTemplate tpl;
    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);

    public RabbitEventPublisher(RabbitTemplate tpl) { this.tpl = tpl; }
    @Override public void publish(String key, Object payload) {
        log.info("[Encounter] publishing event key={} payload={}", key, payload);
        tpl.convertAndSend(RabbitConfig.EXCHANGE, key, payload);
    }
}
