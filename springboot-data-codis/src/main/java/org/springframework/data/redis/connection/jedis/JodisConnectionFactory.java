/*
 * Copyright 2011-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.connection.jedis;

import com.google.common.base.Strings;
import io.codis.jodis.JedisResourcePool;
import io.codis.jodis.RoundRobinJedisPool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.redis.ExceptionTranslationStrategy;
import org.springframework.data.redis.PassThroughExceptionTranslationStrategy;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@NoArgsConstructor
@Getter
public class JodisConnectionFactory implements InitializingBean, DisposableBean, RedisConnectionFactory {

    private static final ExceptionTranslationStrategy EXCEPTION_TRANSLATION = new PassThroughExceptionTranslationStrategy(
            JedisConverters.exceptionConverter());

    private JedisPoolConfig poolConfig = new JedisPoolConfig();
    private JodisConfiguration zookeeperConfig;
    private JedisResourcePool resourcePool;

    private boolean usePool = true;
    private boolean convertPipelineAndTxResults = true;

    public JodisConnectionFactory(JodisConfiguration zookeeperConfig) {
        this.zookeeperConfig = zookeeperConfig;
    }

    public JodisConnectionFactory(JodisConfiguration zookeeperConfig, JedisPoolConfig poolConfig) {
        this.zookeeperConfig = zookeeperConfig;
        this.poolConfig = poolConfig;
    }

    @Override
    public void destroy() {
        if (resourcePool != null) {
            try {
                resourcePool.close();
            } catch (Exception ex) {
                log.warn("Cannot properly close Jedis resource pool", ex);
            }

        }
    }

    @Override
    public void afterPropertiesSet() {
        if (zookeeperConfig != null) {
            resourcePool = createJedisResourcePool();
        }
    }

    @Override
    public RedisConnection getConnection() {
        if (resourcePool != null && usePool) {
            return new JodisConnection(resourcePool);
        }
        throw new InvalidDataAccessApiUsageException("Resource Pool is not configured!");
    }

    @Override
    public RedisClusterConnection getClusterConnection() {
        throw new InvalidDataAccessApiUsageException("Cluster is not configured!");
    }

    @Override
    public boolean getConvertPipelineAndTxResults() {
        return convertPipelineAndTxResults;
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        throw new InvalidDataAccessResourceUsageException("No Sentinels configured");
    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        return EXCEPTION_TRANSLATION.translate(ex);
    }


    private JedisResourcePool createJedisResourcePool() {
        return createJedisResourcePool(this.zookeeperConfig, poolConfig);
    }

    protected JedisResourcePool createJedisResourcePool(JodisConfiguration zookeeperConfig, JedisPoolConfig poolConfig) {
        Assert.notNull(zookeeperConfig, "Zookeeper configuration must not be null!");

        return RoundRobinJedisPool.create()
                .connectionTimeoutMs(zookeeperConfig.getConnectionTimeoutMs())
                .soTimeoutMs(zookeeperConfig.getSoTimeoutMs())
                .curatorClient(zookeeperConfig.getConnectString(), zookeeperConfig.getSessionTimeoutMs())
                .zkProxyDir(zookeeperConfig.getProxyDirection())
                .password(Strings.emptyToNull(zookeeperConfig.getPassword()))
                .poolConfig(poolConfig)
                .build();
    }
}
