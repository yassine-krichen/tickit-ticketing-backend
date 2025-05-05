package com.sofrecom.tickit.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sofrecom.tickit.security.jwt.AuthEntryPointJwt;
import com.sofrecom.tickit.security.jwt.AuthTokenFilter;

/**
 * Test configuration for Spring Security.
 * Disables security for controller tests.
 */
@TestConfiguration
public class TestSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable security for tests
        http.csrf().disable()
            .authorizeRequests().anyRequest().permitAll();
    }
    
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        // Create a test user with admin role
        return new InMemoryUserDetailsManager(
            User.withUsername("test_user")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build()
        );
    }
    
    @Bean
    @Primary
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new MockAuthTokenFilter();
    }
    
    @Bean
    @Primary
    public AuthEntryPointJwt unauthorizedHandler() {
        return new AuthEntryPointJwt();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    /**
     * Mock implementation of AuthTokenFilter that sets a test authentication.
     */
    public static class MockAuthTokenFilter extends AuthTokenFilter {
        @Override
        protected boolean shouldNotFilter(javax.servlet.http.HttpServletRequest request) {
            return false; // Process all requests through this filter
        }
        
        @Override
        protected void doFilterInternal(javax.servlet.http.HttpServletRequest request, 
                                        javax.servlet.http.HttpServletResponse response,
                                        javax.servlet.FilterChain filterChain) 
                                        throws javax.servlet.ServletException, java.io.IOException {
            // For tests, we're bypassing token validation
            filterChain.doFilter(request, response);
        }
    }
}
