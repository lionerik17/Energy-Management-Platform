package com.example.backend_monitor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    public static final String DATA_EXCHANGE = "device.data.exchange";
    public static final String DATA_QUEUE = "device.data.queue";
    public static final String DATA_ROUTING_KEY = "device.data.key";

    public static final String MONITOR_UPDATE = "MONITOR_UPDATE";
    public static final String MONITOR_ALERT = "MONITOR_ALERT";
    public static final String MONITOR_QUEUE = "monitor.update.queue";

    @Bean
    public DirectExchange dataExchange() {
        return new DirectExchange(DATA_EXCHANGE);
    }

    @Bean
    public Queue dataQueue() {
        return new Queue(DATA_QUEUE, true);
    }

    @Bean
    public Binding dataBinding() {
        return BindingBuilder.bind(dataQueue())
                .to(dataExchange())
                .with(DATA_ROUTING_KEY);
    }

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
