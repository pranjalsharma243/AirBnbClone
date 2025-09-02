package com.myprojects.projects.airbnb.ratelimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import io.github.bucket4j.Refill;;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter implements Filter {
    /**
     * Rate limiter that allows 3 requests per minute, with an initial burst of 2 tokens.
     * This is applied to the login endpoint only, and only for POST requests.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    private final String protectedPath = "/api/v1/auth/login"; // exact path
    private final boolean postOnly = true;                      // only POST limit

    private Bucket newBucket() {
        Refill refill = Refill.greedy(3, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(3, refill).withInitialTokens(2);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String clientIp(HttpServletRequest req) {
        // Respect X-Forwarded-For (from NGINX) – first IP is the client
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    private boolean shouldProtect(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if (postOnly && !"POST".equalsIgnoreCase(req.getMethod())) return false;

        if (uri.equals(protectedPath)) return true;



        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if (!shouldProtect(req)) {
            chain.doFilter(request, response);
            return;
        }

        String key = clientIp(req);
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            // Too many requests
            res.setStatus(429);
            res.setHeader("Retry-After", "30");
            res.setContentType("text/plain");
            res.getWriter().write("Too Many Requests");
        }
    }
}
