package com.example.environmental_analyzer.model;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorReadingEvent {
    private String eventId;
    private String sensorId;
    private String type;
    private Double value;
    private Instant timestamp;
}
