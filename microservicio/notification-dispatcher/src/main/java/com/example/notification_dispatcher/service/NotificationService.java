package com.example.notificationdispatcher.service;

import com.example.notificationdispatcher.dto.SensorNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void processNotification(SensorNotificationDTO dto) {
        log.info("🔔 Nueva notificación recibida del sensor: {}", dto);

        if (dto.getType().equalsIgnoreCase("temperatura") && dto.getValue() > 30) {
            log.warn("🔥 Temperatura elevada detectada por el sensor {}: {}°C", dto.getSensorId(), dto.getValue());
            // Aquí podrías enviar correo, SMS, webhook, etc.
        } else {
            log.info("📥 Notificación normal del sensor {}: {} = {}", dto.getSensorId(), dto.getType(), dto.getValue());
        }
    }
}
