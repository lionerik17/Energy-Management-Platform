package com.example.backend_device.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "sync.exchange";
    public static final String QUEUE = "sync.queue.device";
    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String USER_DELETED = "USER_DELETED";

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue syncQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding bindUserCreated() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(USER_CREATED);
    }

    @Bean
    public Binding bindUserUpdated() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(USER_UPDATED);
    }

    @Bean
    public Binding bindUserDeleted() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(USER_DELETED);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonConverter());
        return template;
    }
}