package com.example.environmental_analyzer.repository;

import com.example.environmental_analyzer.model.Alert;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, String> {
    @Query("SELECT DISTINCT a.sensorId FROM Alert a WHERE a.timestamp > :since")
    List<String> findActiveSensorsSince(Instant since);
}
