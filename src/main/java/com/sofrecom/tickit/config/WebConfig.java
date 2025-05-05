package com.sofrecom.tickit.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 * Configuration class for web-related settings including CORS
 * and proper message-converter ordering to prevent Swagger JSON
 * from being Base64-encoded.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    
    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:4200",
                    "http://localhost:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimiterFilter(RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1);
        return reg;
    }

    /**
     * Ensure ByteArray and String converters come before
     * Jackson so JSON isn’t Base64-encoded as byte[].
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 1) Handle raw bytes directly (prevents JSON → Base64) :contentReference[oaicite:0]{index=0}
        converters.add(new ByteArrayHttpMessageConverter());
        // 2) Handle plain text
        converters.add(new StringHttpMessageConverter());
        // 3) Finally, JSON via Jackson
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }
}
