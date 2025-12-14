package com.example.backend_monitor.config;

import com.example.backend_monitor.dtos.DeviceMeasurementEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String SYNC_EXCHANGE = "sync.exchange";
    public static final String SYNC_QUEUE = "sync.queue.monitor";

    public static final String DEVICE_CREATED = "DEVICE_CREATED";
    public static final String DEVICE_UPDATED = "DEVICE_UPDATED";
    public static final String DEVICE_DELETED = "DEVICE_DELETED";
    public static final String DEVICE_ATTACH = "DEVICE_ATTACH";
    public static final String DEVICE_UNATTACH = "DEVICE_UNATTACH";
    public static final String DEVICE_UNATTACH_ALL = "DEVICE_UNATTACH_ALL";

    public static final String MONITOR_UPDATE = "MONITOR_UPDATE";
    public static final String MONITOR_ALERT = "MONITOR_ALERT";
    public static final String MONITOR_QUEUE = "monitor.update.queue";

    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_DELETED = "USER_DELETED";


    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Queue syncQueue() {
        return new Queue(SYNC_QUEUE, true);
    }

    @Bean
    public Binding bindDeviceCreated() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(DEVICE_CREATED);
    }

    @Bean
    public Binding bindDeviceUpdated() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(DEVICE_UPDATED);
    }

    @Bean
    public Binding bindDeviceDeleted() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(DEVICE_DELETED);
    }

    @Bean
    public Binding bindDeviceAttached() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(DEVICE_ATTACH);
    }

    @Bean
    public Binding bindDeviceUnattached() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(DEVICE_UNATTACH);
    }

    @Bean
    public Binding bindDeviceUnattachedAll() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(DEVICE_UNATTACH_ALL);
    }

    @Bean
    public Queue monitorUpdateQueue() {
        return new Queue(MONITOR_QUEUE, true);
    }

    @Bean
    public Binding monitorUpdateBinding() {
        return BindingBuilder.bind(monitorUpdateQueue())
                .to(syncExchange())
                .with(MONITOR_UPDATE);
    }

    @Bean
    public Binding bindUserCreated() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(USER_CREATED);
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
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public ClassMapper classMapper() {
        DefaultClassMapper mapper = new DefaultClassMapper();
        mapper.setDefaultType(DeviceMeasurementEvent.class);
        mapper.setIdClassMapping(Map.of());
        return mapper;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        return factory;
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonConverter());
        return template;
    }
}
