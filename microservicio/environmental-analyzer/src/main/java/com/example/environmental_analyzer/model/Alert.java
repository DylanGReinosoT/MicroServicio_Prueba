package com.example.environmental_analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String alertId;

    private String type;
    private String sensorId;
    private Double value;
    private Double threshold;
    private Instant timestamp;
}
