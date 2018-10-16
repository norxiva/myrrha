package org.springframework.data.redis.connection.jedis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.JodisConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
public class JodisConnectionFactoryTests {
    private static final String ZOOKEEPER_ADDRESS = "zk1-qa.yeshj.com:2181,zk2-qa.yeshj.com:2181,zk3-qa.yeshj.com:2181,zk4-qa.yeshj.com:2181,zk5-qa.yeshj.com:2181";
    private static final String ZOOKEEPER_PROXY_DIRECTION = "/zk/codis/db_QA/proxy";
    private static final int ZOOKEEPER_SESSION_TIMEOUT = 3000;

    private RedisConnection jodisConnection;

    @Before
    public void setUp() {
        JodisConfiguration zookeeperConfiguration = new JodisConfiguration();
        zookeeperConfiguration.setConnectString(ZOOKEEPER_ADDRESS);
        zookeeperConfiguration.setProxyDirection(ZOOKEEPER_PROXY_DIRECTION);
        zookeeperConfiguration.setSessionTimeoutMs(ZOOKEEPER_SESSION_TIMEOUT);

        JodisConnectionFactory factory = new JodisConnectionFactory(zookeeperConfiguration, new JedisPoolConfig());
        factory.afterPropertiesSet();
        jodisConnection = factory.getConnection();
    }

    @After
    public void tearDown() {
        jodisConnection.close();
    }

    @Test
    public void testGetSet() {
        String TEST_KEY = "foe:my:test:key2";
        String TEST_VALUE = "hello world!";
        String get = StringUtils.newStringUtf8(jodisConnection.getSet(
                StringUtils.getBytesUtf8(TEST_KEY),
                StringUtils.getBytesUtf8(TEST_VALUE)));
        //noinspection SpellCheckingInspection
        log.info("getset string: {}", get);
    }

    @Test
    public void testGet() {
        String TEST_KEY = "foe:my:test:key2";
        String TEST_VALUE = "hello world!";
        String get = StringUtils.newStringUtf8(jodisConnection.get(
                StringUtils.getBytesUtf8(TEST_KEY)));
        //noinspection SpellCheckingInspection
        log.info("get string: {}", get);
        Assert.assertNotNull(get);
    }


}
