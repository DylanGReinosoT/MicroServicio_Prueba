package com.example.notificationdispatcher.dto;

import lombok.Data;

@Data
public class SensorNotificationDTO {
    private String sensorId;
    private String type;
    private Double value;
    private String timestamp;
}
