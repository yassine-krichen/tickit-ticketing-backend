package com.sofrecom.tickit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sofrecom.tickit.security.CustomAccessDeniedHandler;
import com.sofrecom.tickit.security.jwt.AuthEntryPointJwt;
import com.sofrecom.tickit.security.jwt.AuthTokenFilter;
import com.sofrecom.tickit.impl.UserDetailsServiceImpl;

/**
 * Configuration class for Spring Security.
 * This class defines the security settings for the application, including authentication and authorization.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	/**
	 * Bean for the JWT authentication token filter.
	 * 
	 * @return AuthTokenFilter instance
	 */
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	/**
	 * Bean for the DAO authentication provider.
	 * Configures the user details service and password encoder.
	 * 
	 * @return DaoAuthenticationProvider instance
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * Bean for the authentication manager.
	 * 
	 * @param authConfig AuthenticationConfiguration instance
	 * @return AuthenticationManager instance
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/**
	 * Bean for the password encoder.
	 * Uses BCrypt for password hashing.
	 * 
	 * @return PasswordEncoder instance
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures the security filter chain.
	 * Defines the HTTP security settings, including CORS, CSRF, session management, and request authorization.
	 * 
	 * @param http HttpSecurity instance
	 * @return SecurityFilterChain instance
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
			.exceptionHandling()
			.authenticationEntryPoint(unauthorizedHandler)
			.accessDeniedHandler(customAccessDeniedHandler()) // Add this line
			.and()
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/api/equipe/**").permitAll()
				.antMatchers("/api/test/**").permitAll()
				.antMatchers("/collaborateur/**").permitAll()
				.antMatchers("/api/attachements/**").permitAll()
				.anyRequest().authenticated();

		http.authenticationProvider(authenticationProvider());
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Bean for the custom access denied handler.
	 * 
	 * @return CustomAccessDeniedHandler instance
	 */
	@Bean
	public CustomAccessDeniedHandler customAccessDeniedHandler() {
		return new CustomAccessDeniedHandler();
	}
}