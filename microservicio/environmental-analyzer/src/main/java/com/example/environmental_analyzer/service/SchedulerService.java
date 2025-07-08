package com.example.environmental_analyzer.service;

import com.example.environmental_analyzer.config.RabbitMQConfig;
import com.example.environmental_analyzer.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final AlertRepository alertRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(cron = "0 0 0 * * *") // Cada día a las 00:00
    public void generateDailyReport() {
        log.info("Generando Daily Report...");
        // Lógica simplificada: en real harías promedios
        rabbitTemplate.convertAndSend(RabbitMQConfig.ALERT_EVENT_EXCHANGE, "alert.routing.key",
                Map.of("event", "DailyReportGenerated", "timestamp", Instant.now()));
    }

    @Scheduled(cron = "0 0 */6 * * *") // Cada 6 horas
    public void checkInactiveSensors() {
        Instant since = Instant.now().minus(24, ChronoUnit.HOURS);
        List<String> activeSensors = alertRepository.findActiveSensorsSince(since);
        // Simula todos los sensores conocidos en el sistema
        List<String> allSensors = List.of("S001", "S002", "S003");

        allSensors.stream()
                .filter(s -> !activeSensors.contains(s))
                .forEach(sensorId -> {
                    log.info("Sensor inactivo detectado: {}", sensorId);
                    rabbitTemplate.convertAndSend(RabbitMQConfig.ALERT_EVENT_EXCHANGE, "alert.routing.key",
                            Map.of("event", "SensorInactiveAlert", "sensorId", sensorId, "timestamp", Instant.now()));
                });
    }

    @Scheduled(cron = "0 0 0 * * MON") // Cada lunes a medianoche
    public void cleanHistoricalData() {
        Instant limit = Instant.now().minus(180, ChronoUnit.DAYS);
        log.info("Limpiando datos históricos antes de {}", limit);
        alertRepository.deleteAll(alertRepository.findAll()
                .stream()
                .filter(a -> a.getTimestamp().isBefore(limit))
                .toList());
    }
}
