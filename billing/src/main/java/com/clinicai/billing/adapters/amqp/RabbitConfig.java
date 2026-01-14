// src/main/java/com/clinicai/billing/adapters/amqp/RabbitConfig.java
package com.clinicai.billing.adapters.amqp;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    public static final String EXCHANGE = "clinicai.events";
    public static final String APPOINTMENT_BOOKED_QUEUE = "appointment.booked.queue";

    @Bean TopicExchange clinicExchange() { return new TopicExchange(EXCHANGE, true, false); }

    @Bean Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        var t = new RabbitTemplate(cf);
        t.setMessageConverter(conv);
        return t;
    }

    @Bean SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        var f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        return f;
    }
}
