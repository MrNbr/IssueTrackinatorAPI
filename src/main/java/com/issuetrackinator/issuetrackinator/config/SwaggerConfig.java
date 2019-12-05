package com.issuetrackinator.issuetrackinator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.issuetrackinator.issuetrackinator"))
                .paths(PathSelectors.any())
                .build()
                .host("localhost:8080")
                .securitySchemes(newArrayList(apiKey()))
                .securityContexts(newArrayList(securityContext()))
                .protocols(newHashSet("http"))
                .produces(newHashSet("application/json"))
                .consumes(newHashSet("application/json"))
                .apiInfo(getApiInfo())
                .tags(new Tag("User controller", "Operations of the user controller"),
                        new Tag("Issue controller", "Operations of the issue controller"),
                        new Tag("Comment controller", "Operations of the comment controller"),
                        new Tag("Attachments", "Operations related to issue attachments"));
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Issue Trackinator API")
                .description("Swagger documentation to test our issue tracker API.")
                .version("1.0.0")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("ApiKeySchema", "api_key", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        return newArrayList(new SecurityReference("ApiKeySchema", new AuthorizationScope[0]));
    }
}
