package com.sofrecom.tickit.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Configuration class for web-related settings including CORS.
 * Allows specific origins with credentials for API access.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    
    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Configure Cross-Origin Resource Sharing (CORS) settings.
     * 
     * @param registry CORS registry to add mappings to
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:4200", // Angular frontend
                    "http://localhost:3000"  // For Bruno or other local tools
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // Cache preflight requests for 1 hour (3600 seconds)
    }
    
    /**
     * Registers the rate limiter filter with a high priority.
     * 
     * @param filter The rate limit filter
     * @return FilterRegistrationBean for the rate limiter
     */
    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimiterFilter(RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1); // run very early
        return reg;
    }
    
    /**
     * Configure message converters to use our properly configured ObjectMapper.
     * This ensures consistent date/time handling across the application.
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        converters.add(0, converter); // Add at index 0 to ensure this is used first
    }
}