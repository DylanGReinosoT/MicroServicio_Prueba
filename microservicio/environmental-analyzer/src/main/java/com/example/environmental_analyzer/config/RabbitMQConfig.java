package com.example.environmental_analyzer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitMQConfig {

    public static final String SENSOR_EVENT_QUEUE = "newSensorReadingQueue";
    public static final String ALERT_EVENT_EXCHANGE = "alertExchange";

    @Bean
    public Queue sensorEventQueue() {
        return new Queue(SENSOR_EVENT_QUEUE, true);
    }

    @Bean
    public DirectExchange alertExchange() {
        return new DirectExchange(ALERT_EVENT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue sensorEventQueue, DirectExchange alertExchange) {
        return BindingBuilder.bind(sensorEventQueue).to(alertExchange).with("sensor.routing.key");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
