package com.clinicai.lablist.adapters.amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
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
    public static final String QUEUE = "lablist.q";
    public static final String ENCOUNTER_COMPLETED = "lab.tests.update";

    @Bean TopicExchange clinicExchange() { return new TopicExchange(EXCHANGE, true, false); }
    @Bean Queue labQueue() { return new Queue(QUEUE, true); }
    @Bean Binding encounterCompletedBinding(Queue labQueue, TopicExchange clinicExchange) {
        return BindingBuilder.bind(labQueue).to(clinicExchange).with(ENCOUNTER_COMPLETED);
    }

    @Bean Jackson2JsonMessageConverter jacksonMessageConverter() { return new Jackson2JsonMessageConverter(); }

    @Bean RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter conv) {
        var tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(conv);
        return tpl;
    }

    @Bean SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf,
                                                                             Jackson2JsonMessageConverter conv) {
        var f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(conv);
        return f;
    }
}
