package com.example.helloagentic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

	private static final Logger log = LoggerFactory.getLogger(VersionController.class);

	private final String version;

	public VersionController(ObjectMapper objectMapper) throws IOException {
		try (InputStream is = getClass().getResourceAsStream("/version.json")) {
			if (is == null) {
				throw new IOException("version.json not found on classpath");
			}
			JsonNode node = objectMapper.readTree(is);
			this.version = node.get("version").asText();
		}
		log.info("Loaded application version: {}", version);
	}

	@GetMapping("/version")
	public Map<String, String> version() {
		log.info("Received version request");
		return Map.of("version", version);
	}

}
