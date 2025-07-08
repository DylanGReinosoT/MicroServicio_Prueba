package com.example.environmental_analyzer.service;

import com.example.environmental_analyzer.config.RabbitMQConfig;
import com.example.environmental_analyzer.model.Alert;
import com.example.environmental_analyzer.model.AlertEvent;
import com.example.environmental_analyzer.model.SensorReadingEvent;
import com.example.environmental_analyzer.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzerService {

    private final AlertRepository alertRepository;
    private final RabbitTemplate rabbitTemplate;

    public void processSensorReading(SensorReadingEvent event) {
        if (event == null) return;

        String alertType = null;
        Double threshold = null;

        if ("temperature".equalsIgnoreCase(event.getType()) && event.getValue() > 40.0) {
            alertType = "HighTemperatureAlert";
            threshold = 40.0;
        } else if ("humidity".equalsIgnoreCase(event.getType()) && event.getValue() < 20.0) {
            alertType = "LowHumidityWarning";
            threshold = 20.0;
        } else if ("seismic".equalsIgnoreCase(event.getType()) && event.getValue() > 3.0) {
            alertType = "SeismicActivityDetected";
            threshold = 3.0;
        }

        if (alertType != null) {
            Alert alert = Alert.builder()
                .type(alertType)
                .sensorId(event.getSensorId())
                .value(event.getValue())
                .threshold(threshold)
                .timestamp(Instant.now())
                .build();

            alertRepository.save(alert);

            // Emitir alerta a RabbitMQ
            AlertEvent alertEvent = AlertEvent.builder()
                .alertId(alert.getAlertId())
                .type(alert.getType())
                .sensorId(alert.getSensorId())
                .value(alert.getValue())
                .threshold(alert.getThreshold())
                .timestamp(alert.getTimestamp())
                .build();

            rabbitTemplate.convertAndSend(RabbitMQConfig.ALERT_EVENT_EXCHANGE, "alert.routing.key", alertEvent);
            log.info("Alerta generada y enviada: {}", alertEvent);
        }
    }
}
