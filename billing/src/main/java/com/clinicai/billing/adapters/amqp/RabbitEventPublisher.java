package com.clinicai.billing.adapters.amqp;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.clinicai.billing.app.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component class RabbitEventPublisher implements EventPublisher{
    private final RabbitTemplate tpl;
    private static final Logger log = LoggerFactory.getLogger(RabbitEventPublisher.class);
    RabbitEventPublisher(RabbitTemplate t){ this.tpl=t; }
    public void publish(String key, Object payload){
        log.info("[Billing] publishing event key={} payload={}", key, payload);
        tpl.convertAndSend(RabbitConfig.EXCHANGE, key, payload);
    }
}
