package com.clinicai.encounter.adapters.amqp;

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
    public static final String BILLING_QUEUE = "encounter.billing.q";
    public static final String BILLING_PAYMENT_KEY = "billing.payment.authorized";

    @Bean
    TopicExchange clinicExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue billingPaymentQueue() {
        return new Queue(BILLING_QUEUE, true);
    }

    @Bean
    Binding billingPaymentBinding(Queue billingPaymentQueue, TopicExchange clinicExchange) {
        return BindingBuilder.bind(billingPaymentQueue).to(clinicExchange).with(BILLING_PAYMENT_KEY);
    }

    @Bean
    Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter converter) {
        var template = new RabbitTemplate(cf);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            Jackson2JsonMessageConverter converter) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        return factory;
    }
}
