package org.springframework.boot.autoconfigure.data.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.boot.autoconfigure.data.redis.JodisProperties.PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = PREFIX)
public class JodisProperties {
    public static final String PREFIX = "spring.redis.jodis";

    private boolean enabled;
    private String connectString;
    private String proxyDirection;

    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private int soTimeoutMs;

    private String password;


}
