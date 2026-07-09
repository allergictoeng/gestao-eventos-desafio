package br.com.petro.eventos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = "classpath:messages.properties", encoding = "UTF-8")
public class OpenApiConfig {

    private final Environment env;

    public OpenApiConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(env.getProperty("api.info.title"))
                        .version("1.0")
                        .description(env.getProperty("api.info.description")));
    }
}