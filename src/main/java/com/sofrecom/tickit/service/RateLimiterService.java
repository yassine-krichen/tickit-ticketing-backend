package com.sofrecom.tickit.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory Bucket4j limiter, with thresholds driven from application.yml.
 */
@Service
public class RateLimiterService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private Bandwidth limit;

    // inject with defaults (in case you forget to set in properties)
    @Value("${ratelimit.capacity:50}")
    private long capacity;

    @Value("${ratelimit.refillTokens:50}")
    private long refillTokens;

    @Value("${ratelimit.refillPeriod:PT1M}")
    private Duration refillPeriod;

    @PostConstruct
    public void init() {
        // build your limit once, then reuse
        this.limit = Bandwidth.classic(
            capacity,
            Refill.greedy(refillTokens, refillPeriod)
        );
    }

    /**
     * Get or create the bucket for the given key (IP / userId).
     */
    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> Bucket.builder()
                                                      .addLimit(limit)
                                                      .build());
    }
}
