package SensorDataCollector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class SensorDataCollectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensorDataCollectorApplication.class, args);
	}

}
