package com.example.backend_user.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "sync.exchange";
    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String USER_DELETED = "USER_DELETED";
    public static final String USER_PROFILE_READY = "USER_PROFILE_READY";

    public static final String USER_SYNC_QUEUE = "sync.queue.user";
    public static final String PROFILE_READY_QUEUE = "sync.queue.profile.ready";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue userSyncQueue() {
        return new Queue(USER_SYNC_QUEUE, true);
    }

    @Bean
    public Queue profileReadyQueue() {
        return new Queue(PROFILE_READY_QUEUE, true);
    }

    @Bean
    public Binding bindUserCreated() {
        return BindingBuilder.bind(userSyncQueue())
                .to(exchange())
                .with(USER_CREATED);
    }

    @Bean
    public Binding bindUserUpdated() {
        return BindingBuilder.bind(userSyncQueue())
                .to(exchange())
                .with(USER_UPDATED);
    }

    @Bean
    public Binding bindUserDeleted() {
        return BindingBuilder.bind(userSyncQueue())
                .to(exchange())
                .with(USER_DELETED);
    }

    @Bean
    public Binding bindProfileReady() {
        return BindingBuilder.bind(profileReadyQueue())
                .to(exchange())
                .with(USER_PROFILE_READY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(syncEventClassMapper());
        return converter;
    }

    @Bean
    public ClassMapper syncEventClassMapper() {
        DefaultClassMapper mapper = new DefaultClassMapper();

        mapper.setDefaultType(com.example.backend_user.dtos.SyncEvent.class);

        mapper.setIdClassMapping(Map.of());

        return mapper;
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate t = new RabbitTemplate(connectionFactory);
        t.setMessageConverter(messageConverter());
        t.setBeforePublishPostProcessors(message -> {
            message.getMessageProperties().getHeaders().remove("__TypeId__");
            return message;
        });
        return t;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

}
