package com.redhorse.deokhugam.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "Deokhugam API 명세서",
                description = "Deokhugam API 명세서입니다.",
                version = "v0.1"
        )
)
@Configuration
public class SwaggerConfig {

}
