package com.example.helloagentic;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

	private static final Logger log = LoggerFactory.getLogger(PingController.class);

	@GetMapping("/ping")
	public Map<String, String> ping() {
		log.info("Received ping request");
		return Map.of("pong", "Hello from Agentic!");
	}

}
