package org.springframework.data.redis.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JodisConfiguration {
    private String connectString;
    private String proxyDirection;
    private int connectionTimeoutMs;
    private int soTimeoutMs;
    private int sessionTimeoutMs;
    private String password;
}
