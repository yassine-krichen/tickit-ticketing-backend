package com.sofrecom.tickit.config;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sofrecom.tickit.service.RateLimiterService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Rejects when the bucket has no tokens left.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    @Autowired
    public RateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
        throws ServletException, IOException {

        // Simple client key: remote IP; you can swap to userId after auth is in place
        String clientKey = request.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(clientKey);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests – try again later.");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // optionally skip health-check or static resources
        return request.getRequestURI().startsWith("/actuator/health");
    }
}
