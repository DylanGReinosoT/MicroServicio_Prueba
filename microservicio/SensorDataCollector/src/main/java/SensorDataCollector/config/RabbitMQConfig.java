package SensorDataCollector.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "sensor.exchange";
    public static final String QUEUE_NAME = "sensor.events";
    public static final String ROUTING_KEY = "sensor.routingkey";

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public DirectExchange sensorExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue sensorEventsQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-queue-type", "quorum") // Para persistencia
                .build();
    }

    @Bean
    public Binding sensorEventsBinding() {
        return BindingBuilder.bind(sensorEventsQueue())
                .to(sensorExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setExchange(EXCHANGE_NAME);
        rabbitTemplate.setRoutingKey(ROUTING_KEY);

        // Configuración de reintentos
        RetryTemplate retryTemplate = new RetryTemplate();
        rabbitTemplate.setRetryTemplate(retryTemplate);

        // Confirmación de mensajes
        rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
            if (!ack) {
                System.err.println("Message failed to reach exchange. Reason: " + reason);
            }
        });

        return rabbitTemplate;
    }
}