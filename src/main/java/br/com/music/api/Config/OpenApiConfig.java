package br.com.music.api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                    .title("Music API")
                    .description("REST API for managing artists and albums")
                    .version("1.0.0")
                    .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT")));
    }
}
