package com.stanum.skrudzh.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stanum.skrudzh.controller.helpers.Interceptor;
import com.stanum.skrudzh.jpa.repository.SessionRepository;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.utils.auth.JwtTokenConfig;
import com.stanum.skrudzh.utils.auth.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
class WebConfig implements WebMvcConfigurer {

    @Value("${threebaskets.jwtKey.private}")
    private String jwtSignKeyPriv;

    @Value("${threebaskets.jwtKey.public}")
    private String jwtPubKey;

    @Value("${threebaskets.jwt.issuedBy}")
    private String jwtIssuedBy;

    @Value("${threebaskets.jwt.lifetime}")
    private int jwtLifetime;

    private final SessionRepository sessionRepository;

    private final UserRepository userRepository;

    @Bean
    Interceptor authInterceptor() {
        return new Interceptor(sessionRepository, userRepository);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor());
    }


    @Bean
    public ClassLoaderTemplateResolver templateResolver() {

        var templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html**").addResourceLocations("classpath:/static/");
    }

    private PrivateKey getJwtSigningKey() {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encodedKey = decoder.decode(jwtSignKeyPriv);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    JwtTokenConfig jwtTokenConfig() {
        return new JwtTokenConfig(jwtIssuedBy, jwtLifetime, this.getJwtSigningKey(), JwtTokenUtil.getJwtPublicKey(jwtPubKey));
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}

