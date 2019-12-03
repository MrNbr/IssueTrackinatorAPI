package com.issuetrackinator.issuetrackinator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        Set<String> protocols = new HashSet<>();
        protocols.add("http");

        Set<String> mimeTypes = new HashSet<>();
        mimeTypes.add("application/json");

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.issuetrackinator.issuetrackinator"))
                .paths(PathSelectors.any())
                .build()
                .host("localhost:8080")
                .protocols(protocols)
                .produces(mimeTypes)
                .consumes(mimeTypes)
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Issue Trackinator API")
                .description("Swagger documentation to test our issue tracker API.")
                .version("1.0.0")
                .build();
    }
}
