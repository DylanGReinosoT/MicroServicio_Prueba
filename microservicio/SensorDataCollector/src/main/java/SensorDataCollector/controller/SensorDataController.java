package SensorDataCollector.controller;

import SensorDataCollector.dto.SensorReadingDTO;
import SensorDataCollector.model.SensorReading;
import SensorDataCollector.service.SensorDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensor-readings")
@RequiredArgsConstructor
public class SensorDataController {

    private final SensorDataService sensorDataService;

    @PostMapping
    public ResponseEntity<SensorReading> createReading(@Valid @RequestBody SensorReadingDTO dto) {
        return ResponseEntity.ok(sensorDataService.saveReading(dto));
    }

    @GetMapping("/{sensorId}")
    public ResponseEntity<List<SensorReading>> getReadings(@PathVariable String sensorId) {
        return ResponseEntity.ok(sensorDataService.getReadingsBySensor(sensorId));
    }

    @GetMapping
    public ResponseEntity<List<SensorReading>> getAllReadings() {
        return ResponseEntity.ok(sensorDataService.getAllReadings());
    }
}

