package org.springframework.boot.autoconfigure.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Map;

@Slf4j
public class RedisCacheExtraManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {

    private Map<String, Long> expires;

    public RedisCacheExtraManagerCustomizer(Map<String, Long> expires) {
        this.expires = expires;
    }

    @Override
    public void customize(RedisCacheManager cacheManager) {
        if (expires != null) {
            cacheManager.setExpires(expires);
        }

    }
}
