package com.example.environmental_analyzer.listener;

import com.example.environmental_analyzer.model.AlertEvent;
import com.example.environmental_analyzer.model.SensorReadingEvent;
import com.example.environmental_analyzer.service.AnalyzerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventListener {

    private final AnalyzerService analyzerService;
    private final RabbitTemplate rabbitTemplate; // Inyectar RabbitTemplate
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "newSensorReadingQueue")
    public void receiveSensorReading(Message message) {
        try {
            // Configurar ObjectMapper para tipos de tiempo de Java 8
            objectMapper.registerModule(new JavaTimeModule());

            // Deserializar el mensaje
            SensorReadingEvent event = objectMapper.readValue(
                    message.getBody(),
                    SensorReadingEvent.class
            );

            log.info("Evento recibido: {}", event);

            // Procesar lectura y generar alertas si es necesario
            processAndPublishAlerts(event);

        } catch (Exception e) {
            log.error("Error procesando mensaje: {}", new String(message.getBody()), e);
            // Aquí podrías implementar lógica para manejar mensajes fallidos
        }
    }

    private void processAndPublishAlerts(SensorReadingEvent event) {
        // 1. Procesar la lectura
        analyzerService.processSensorReading(event);

        // 2. Generar alertas según los umbrales
        if ("temperature".equals(event.getType()) && event.getValue() > 40.0) {
            publishHighTemperatureAlert(event);
        }
        // Agregar más condiciones para otros tipos de alertas...
    }

    private void publishHighTemperatureAlert(SensorReadingEvent event) {
        AlertEvent alert = AlertEvent.builder()
                .alertId(UUID.randomUUID().toString())
                .sensorId(event.getSensorId())
                .value(event.getValue())
                .threshold(40.0)
                .timestamp(Instant.now())
                .build();

        try {
            // Convertir a JSON
            String alertJson = objectMapper.writeValueAsString(alert);

            // Publicar en la cola de alertas
            rabbitTemplate.convertAndSend(
                    "alertEventsExchange", // Nombre del exchange
                    "high.temp.alert",   // Routing key
                    alertJson,            // Cuerpo del mensaje
                    message -> {
                        MessageProperties props = message.getMessageProperties();
                        props.setContentType("application/json");
                        return message;
                    }
            );

            log.info("Alerta de temperatura alta publicada: {}", alert);

        } catch (JsonProcessingException e) {
            log.error("Error serializando alerta: {}", alert, e);
        }
    }
}
