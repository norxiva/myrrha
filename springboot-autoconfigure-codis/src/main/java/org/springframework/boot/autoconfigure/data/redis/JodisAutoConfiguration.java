package org.springframework.boot.autoconfigure.data.redis;

import io.codis.jodis.RoundRobinJedisPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.JodisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JodisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@SuppressWarnings("SpringFacetCodeInspection")
@Configuration
@ConditionalOnClass({JedisConnection.class, RedisOperations.class, Jedis.class, RoundRobinJedisPool.class})
@EnableConfigurationProperties(JodisProperties.class)
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class JodisAutoConfiguration {

    @Configuration
    @ConditionalOnClass(GenericObjectPool.class)
    @ConditionalOnProperty(prefix = "spring.redis.jodis", name = "enabled", havingValue = "true", matchIfMissing = true)
    protected static class RedisConnectionConfiguration {

        private final RedisProperties properties;
        private final JodisProperties jodisProperties;

        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        @Autowired
        public RedisConnectionConfiguration(RedisProperties properties,
                                            JodisProperties jodisProperties) {
            this.properties = properties;
            this.jodisProperties = jodisProperties;
        }

        @Bean
        @ConditionalOnMissingBean(RedisConnectionFactory.class)
        public JodisConnectionFactory jodisConnectionFactory() {
            return createJodisConnectionFactory();
        }

        private JodisConfiguration getZookeeperConfig() {
            if (jodisProperties.getConnectString() != null) {
                JodisConfiguration config = new JodisConfiguration();
                config.setConnectString(jodisProperties.getConnectString());
                config.setProxyDirection(jodisProperties.getProxyDirection());
                config.setSessionTimeoutMs(jodisProperties.getSessionTimeoutMs());
                config.setConnectionTimeoutMs(jodisProperties.getConnectionTimeoutMs());
                config.setSoTimeoutMs(jodisProperties.getSoTimeoutMs());
                config.setPassword(jodisProperties.getPassword());
                return config;
            }
            return null;
        }

        private JodisConnectionFactory createJodisConnectionFactory() {
            JedisPoolConfig poolConfig = (this.properties.getPool() != null)
                    ? jedisPoolConfig() : new JedisPoolConfig();

            return new JodisConnectionFactory(getZookeeperConfig(), poolConfig);
        }

        private JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig config = new JedisPoolConfig();
            RedisProperties.Pool props = this.properties.getPool();
            config.setMaxTotal(props.getMaxActive());
            config.setMaxIdle(props.getMaxIdle());
            config.setMinIdle(props.getMinIdle());
            config.setMaxWaitMillis(props.getMaxWait());
            return config;
        }

    }
}
