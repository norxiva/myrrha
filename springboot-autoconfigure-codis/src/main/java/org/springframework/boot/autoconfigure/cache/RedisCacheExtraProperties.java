package org.springframework.boot.autoconfigure.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.boot.autoconfigure.cache.RedisCacheExtraProperties.PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = PREFIX)
public class RedisCacheExtraProperties {
    public static final String PREFIX = "spring.cache.redis.extra";

    private Map<String, Long> expires = new HashMap<>();
}
