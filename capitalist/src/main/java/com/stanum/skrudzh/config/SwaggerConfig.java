package com.stanum.skrudzh.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.stanum.skrudzh.utils.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@EnableSwagger2
@Configuration
@ComponentScan(basePackages = "com.stanum.skrudzh")
public class SwaggerConfig {

    private static ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Skrudzh API")
                .description("Skrudzh API")
                .license("")
                .licenseUrl("")
                .termsOfServiceUrl("")
                .version("0.2").build();
    }

    private static ApiKey apiKey() {
        return new ApiKey(Constants.JWT_AUTH, Constants.JWT_KEY, "header");
    }

    private static Predicate<String> paths() {
        return Predicates.and(
                Predicates.not(PathSelectors.regex("/error.*"))
                , Predicates.not(PathSelectors.regex("/code.*"))
                //, Predicates.not(PathSelectors.regex("/actuator.*"))
        );
    }

    private static SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/*"))
                .build();
    }

    private static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> result = new ArrayList<>();
        result.add(new SecurityReference(Constants.JWT_KEY, authorizationScopes));
        return result;
    }

    @Bean
    Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("Skrudzh API")
                .apiInfo(apiInfo())
                .securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Lists.newArrayList(securityContext()))
                .select()
                .paths(paths())
                .build();
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .validatorUrl(null)
                .build();
    }

}