package org.springframework.boot.autoconfigure.cache;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@Configuration
@AutoConfigureBefore(RedisCacheConfiguration.class)
@ConditionalOnBean(RedisTemplate.class)
@Conditional(CacheCondition.class)
@EnableConfigurationProperties(RedisCacheExtraProperties.class)
public class RedisCacheExtraConfiguration {

    private RedisCacheExtraProperties properties;

    public RedisCacheExtraConfiguration(RedisCacheExtraProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RedisCacheExtraManagerCustomizer redisCacheExtraManagerCustomizer() {
        return new RedisCacheExtraManagerCustomizer(properties.getExpires());
    }
}
