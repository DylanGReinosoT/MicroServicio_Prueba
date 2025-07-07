package SensorDataCollector.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewSensorReadingEvent implements Serializable {
    private String eventId;
    private String sensorId;
    private String type;
    private Double value;
    private Instant timestamp;
}
