package org.springframework.data.redis.connection;

import io.codis.jodis.JedisResourcePool;
import redis.clients.jedis.Jedis;

public interface JodisResourceProvider {
    <S> S getResource(JedisResourcePool resourcePool);

    void returnResource(Jedis resource);
}
