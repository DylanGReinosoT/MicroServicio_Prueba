package SensorDataCollector.service;

import SensorDataCollector.dto.SensorReadingDTO;
import SensorDataCollector.event.NewSensorReadingEvent;
import SensorDataCollector.model.SensorReading;
import SensorDataCollector.repository.SensorReadingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SensorDataService {

    private final SensorReadingRepository repository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Guarda una nueva lectura y publica el evento en RabbitMQ.
     */
    @Transactional
    public SensorReading saveReading(SensorReadingDTO dto) {
        SensorReading reading = new SensorReading();
        reading.setSensorId(dto.getSensorId());
        reading.setType(dto.getType());
        reading.setValue(dto.getValue());
        reading.setTimestamp(dto.getTimestamp());

        SensorReading savedReading = repository.save(reading);

        NewSensorReadingEvent event = new NewSensorReadingEvent(
                UUID.randomUUID().toString(),
                savedReading.getSensorId(),
                savedReading.getType(),
                savedReading.getValue(),
                savedReading.getTimestamp()
        );

        try {
            rabbitTemplate.convertAndSend("sensor.events", event);
            savedReading.setProcessed(true);
            repository.save(savedReading);
        } catch (AmqpException e) {
            // Si falla RabbitMQ, el processed queda en false para reintentar luego
        }

        return savedReading;
    }

    /**
     * Devuelve todas las lecturas de un sensor específico.
     */
    public List<SensorReading> getReadingsBySensor(String sensorId) {
        return repository.findBySensorId(sensorId);
    }

    /**
     * Devuelve todas las lecturas almacenadas.
     */
    public List<SensorReading> getAllReadings() {
        return repository.findAll();
    }

    /**
     * Reintenta enviar los eventos no procesados cada 30 segundos.
     */
    @Scheduled(fixedRate = 30000)
    @Transactional
    public void retryFailedEvents() {
        List<SensorReading> unprocessed = repository.findByProcessedFalse();

        unprocessed.forEach(reading -> {
            NewSensorReadingEvent event = new NewSensorReadingEvent(
                    UUID.randomUUID().toString(),
                    reading.getSensorId(),
                    reading.getType(),
                    reading.getValue(),
                    reading.getTimestamp()
            );

            try {
                rabbitTemplate.convertAndSend("sensor.events", event);
                reading.setProcessed(true);
                repository.save(reading);
            } catch (AmqpException e) {
                // Loguear o manejar el fallo si es necesario
            }
        });
    }
}
