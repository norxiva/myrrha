package org.springframework.data.redis.connection.jedis;

import io.codis.jodis.RoundRobinJedisPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

@Slf4j
public class JodisConnectionTests {

    private static final String ZOOKEEPER_ADDRESS = "zk1-qa.yeshj.com:2181,zk2-qa.yeshj.com:2181,zk3-qa.yeshj.com:2181,zk4-qa.yeshj.com:2181,zk5-qa.yeshj.com:2181";
    private static final String ZOOKEEPER_PROXY_DIRECTION = "/zk/codis/db_QA/proxy";
    private static final int ZOOKEEPER_SESSION_TIMEOUT = 3000;

    private JodisConnection jodisConnection;

    @Before
    public void setUp() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jodisConnection = new JodisConnection(RoundRobinJedisPool
                .create()
                .curatorClient(ZOOKEEPER_ADDRESS, ZOOKEEPER_SESSION_TIMEOUT)
                .zkProxyDir(ZOOKEEPER_PROXY_DIRECTION)
                .poolConfig(jedisPoolConfig)
                .build());
    }

    @After
    public void tearDown() throws IOException {
        jodisConnection.close();
    }

    @Test
    public void testGetSet() {
        String TEST_KEY = "foe:my:test:key1";
        String TEST_VALUE = "hello world!";
        String get = StringUtils.newStringUtf8(jodisConnection.getSet(
                StringUtils.getBytesUtf8(TEST_KEY),
                StringUtils.getBytesUtf8(TEST_VALUE)));
        //noinspection SpellCheckingInspection
        log.info("getset string: {}", get);
        Assert.assertNotNull(get);
    }

    @Test
    public void testGet() {
        String TEST_KEY = "foe:my:test:key1";
        String TEST_VALUE = "hello world!";
        String get = StringUtils.newStringUtf8(jodisConnection.get(
                StringUtils.getBytesUtf8(TEST_KEY)));
        //noinspection SpellCheckingInspection
        log.info("get string: {}", get);
        Assert.assertEquals(get,TEST_VALUE);
    }




}
