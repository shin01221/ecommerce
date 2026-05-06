package com.ecommerce.util;

import com.ecommerce.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class RateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    private static final int MAX_REQUESTS = 20;
    private static final int WINDOW_SECONDS = 60;

    public static boolean isAllowed(String clientIp) {
        String key = "rate_limit:" + clientIp;
        try (Jedis jedis = RedisConfig.getConnection()) {
            long count = jedis.incr(key);
            if (count == 1) {
                jedis.expire(key, WINDOW_SECONDS);
            }
            boolean allowed = count <= MAX_REQUESTS;
            if (!allowed) {
                logger.warn("Rate limit exceeded for IP: {}", clientIp);
            }
            return allowed;
        } catch (Exception e) {
            logger.error("Rate limiter error for IP: {}", clientIp, e);
            return true;
        }
    }
}
