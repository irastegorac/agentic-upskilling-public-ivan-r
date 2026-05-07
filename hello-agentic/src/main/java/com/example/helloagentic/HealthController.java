package com.example.helloagentic;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	private static final Logger log = LoggerFactory.getLogger(HealthController.class);

	@GetMapping("/health")
	public Map<String, Object> health() {
		log.info("Received health request");
		long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
		Duration uptime = Duration.ofMillis(uptimeMillis);
		String formattedUptime = String.format("%dd %dh %dm %ds",
				uptime.toDays(),
				uptime.toHoursPart(),
				uptime.toMinutesPart(),
				uptime.toSecondsPart());
		return Map.of(
				"status", "UP",
				"uptimeMillis", uptimeMillis,
				"uptime", formattedUptime
		);
	}

}
