package com.example.backend_ws.config;

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
    public static final String SYNC_QUEUE = "sync.ws.queue";

    public static final String CHAT_TO_ADMIN = "CHAT_TO_ADMIN";
    public static final String CHAT_TO_USER = "CHAT_TO_USER";
    public static final String BOT_TO_USER = "BOT_TO_USER";

    public static final String QUEUE_CHAT_TO_ADMIN = "chat.to.admin.queue";
    public static final String QUEUE_CHAT_TO_USER = "chat.to.user.queue";
    public static final String QUEUE_BOT_TO_USER = "bot.to.user.queue";

    public static final String MONITOR_UPDATE = "MONITOR_UPDATE";
    public static final String MONITOR_ALERT = "MONITOR_ALERT";
    public static final String MONITOR_QUEUE = "monitor.update.queue";

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(SYNC_EXCHANGE);
    }

    @Bean
    public Queue queueChatToAdmin() {
        return new Queue(QUEUE_CHAT_TO_ADMIN, true);
    }

    @Bean
    public Queue queueChatToUser() {
        return new Queue(QUEUE_CHAT_TO_USER, true);
    }

    @Bean
    public Queue queueBotToUser() {
        return new Queue(QUEUE_BOT_TO_USER, true);
    }

    @Bean
    public Queue syncQueue() {
        return new Queue(SYNC_QUEUE, true);
    }

    @Bean
    public Queue monitorQueue() {
        return new Queue(MONITOR_QUEUE, true);
    }

    @Bean
    public Binding bindChatToAdmin() {
        return BindingBuilder.bind(queueChatToAdmin())
                .to(syncExchange())
                .with(CHAT_TO_ADMIN);
    }

    @Bean
    public Binding bindChatToUser() {
        return BindingBuilder.bind(queueChatToUser())
                .to(syncExchange())
                .with(CHAT_TO_USER);
    }

    @Bean
    public Binding bindBotToUser() {
        return BindingBuilder.bind(queueBotToUser())
                .to(syncExchange())
                .with(BOT_TO_USER);
    }

    @Bean
    public Binding bindMonitorUpdate() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(MONITOR_UPDATE);
    }

    @Bean
    public Binding monitorAlertBinding() {
        return BindingBuilder.bind(syncQueue())
                .to(syncExchange())
                .with(RabbitMQConfig.MONITOR_ALERT);
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
