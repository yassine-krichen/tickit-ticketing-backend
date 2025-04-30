package com.sofrecom.tickit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for CORS settings.
 * Allows specific origins with credentials for API access.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow specific origins
        config.addAllowedOrigin("http://localhost:4200"); // Angular frontend
        config.addAllowedOrigin("http://localhost:3000"); // For Bruno or other local tools
        
        // Enable credentials
        config.setAllowCredentials(true);
        
        // Allow standard methods and headers
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        // Cache preflight requests for 1 hour (3600 seconds)
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}