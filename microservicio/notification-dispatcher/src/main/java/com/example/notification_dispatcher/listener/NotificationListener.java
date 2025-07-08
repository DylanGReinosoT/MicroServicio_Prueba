package com.example.notificationdispatcher.listener;

import com.example.notificationdispatcher.dto.SensorNotificationDTO;
import com.example.notificationdispatcher.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleNotification(SensorNotificationDTO dto) {
        notificationService.processNotification(dto);
    }
}
