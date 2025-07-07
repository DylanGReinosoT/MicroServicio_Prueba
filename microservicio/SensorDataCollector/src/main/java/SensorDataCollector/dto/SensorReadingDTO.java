package SensorDataCollector.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReadingDTO {
    @NotBlank
    private String sensorId;

    @NotBlank
    private String type;

    @NotNull
    @DecimalMin("-50.0") @DecimalMax("60.0")
    private Double value;

    @NotNull
    private Instant timestamp;
}
