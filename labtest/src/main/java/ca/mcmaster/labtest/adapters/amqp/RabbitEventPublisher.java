package ca.mcmaster.labtest.adapters.amqp;

import ca.mcmaster.labtest.app.EventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitEventPublisher implements EventPublisher {
    private final RabbitTemplate tpl;

    public RabbitEventPublisher(RabbitTemplate tpl) {
        this.tpl = tpl;
    }

    @Override
    public void publish(String key, Object payload) {
        tpl.convertAndSend(RabbitConfig.EXCHANGE, key, payload);
    }
}
