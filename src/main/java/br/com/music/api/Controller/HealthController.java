package br.com.music.api.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping("")
public class HealthController {

    @GetMapping
    @Hidden
    public String health() {
        return "Music API is running! Visit /api/v1/swagger-ui.html for API documentation.";
    }

    @GetMapping("/health")
    @Hidden
    public String healthCheck() {
        return "OK";
    }
}
