package com.sofrecom.tickit.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sofrecom.tickit.impl.UserDetailsServiceImpl;

/**
 * Filter to handle JWT authentication for incoming requests.
 * This filter intercepts requests, validates the JWT token, and sets the authentication context.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	/**
	 * Filters incoming requests to validate JWT tokens and set the security context.
	 *
	 * @param request     The HTTP request.
	 * @param response    The HTTP response.
	 * @param filterChain The filter chain to pass the request/response to the next filter.
	 * @throws ServletException If a servlet-specific error occurs.
	 * @throws IOException      If an input or output error occurs.
	 */
	@SuppressWarnings("null")
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			// Extract and validate the JWT token
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				// Retrieve username from the token
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

				// Load user details and set authentication context
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			logger.error("Cannot set user authentication: {}", e.getMessage());
		}

		// Continue with the filter chain
		filterChain.doFilter(request, response);
	}

	/**
	 * Extracts the JWT token from the Authorization header of the request.
	 *
	 * @param request The HTTP request.
	 * @return The JWT token if present and valid, otherwise null.
	 */
	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		// Check if the Authorization header contains a Bearer token
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7); // Extract the token after "Bearer "
		}

		return null;
	}
}