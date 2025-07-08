package com.example.environmental_analyzer.model;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertEvent {
    private String alertId;
    private String type;
    private String sensorId;
    private Double value;
    private Double threshold;
    private Instant timestamp;
}
