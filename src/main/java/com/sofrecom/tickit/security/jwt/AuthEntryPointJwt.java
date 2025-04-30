package com.sofrecom.tickit.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom implementation of {@link AuthenticationEntryPoint} to handle unauthorized access attempts.
 * This class is invoked whenever an unauthenticated user tries to access a secured resource.
 * It returns a JSON response with error details instead of the default HTML error page.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

	/**
	 * Handles unauthorized access by sending a JSON response with error details.
	 *
	 * @param request       The HTTP request that resulted in an {@link AuthenticationException}.
	 * @param response      The HTTP response to be sent to the client.
	 * @param authException The exception thrown when authentication fails.
	 * @throws IOException      If an input or output error occurs while writing the response.
	 * @throws ServletException If a servlet-specific error occurs.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		// Log the unauthorized access attempt
		logger.error("Unauthorized error: {}", authException.getMessage());

		// Set response content type and status
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// Prepare the response body with error details
		Map<String, Object> body = new HashMap<>();
		body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
		body.put("error", "Unauthorized");
		body.put("message", authException.getMessage());
		body.put("path", request.getServletPath());

		// Write the response body as JSON
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}
}