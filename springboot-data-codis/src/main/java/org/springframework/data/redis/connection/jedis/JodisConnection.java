package org.springframework.data.redis.connection.jedis;

import io.codis.jodis.JedisResourcePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.ExceptionTranslationStrategy;
import org.springframework.data.redis.FallbackExceptionTranslationStrategy;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanCursor;
import org.springframework.data.redis.core.ScanIteration;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JodisConnection implements RedisClusterConnection {

    private static final ExceptionTranslationStrategy EXCEPTION_TRANSLATION = new FallbackExceptionTranslationStrategy(
            JedisConverters.exceptionConverter());

    private final JedisResourcePool resourcePool;
    private JodisCommandExecutor executor;
    private boolean closed;

    private volatile JedisSubscription subscription;

    public JodisConnection(JedisResourcePool resourcePool) {
        this.resourcePool = resourcePool;

        closed = false;
        this.executor = new JodisCommandExecutor();
    }

    @Override
    public void close() throws DataAccessException {
        if (!closed) {
            try {
                resourcePool.getResource().close();
            } catch (Exception ex) {
                log.warn("Cannot properly close jodis resource pool", ex);
            }
        }

        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public JedisResourcePool getNativeConnection() {
        return resourcePool;
    }

    @Override
    public boolean isQueueing() {
        return false;
    }

    @Override
    public boolean isPipelined() {
        return false;
    }

    @Override
    public void openPipeline() {
        throw new UnsupportedOperationException("Pipeline is currently not supported for JodisConnection.");
    }

    @Override
    public List<Object> closePipeline() throws RedisPipelineException {
        throw new UnsupportedOperationException("Pipeline is currently not supported for JodisConnection.");
    }

    @Override
    public RedisSentinelConnection getSentinelConnection() {
        throw new UnsupportedOperationException("Sentinel is currently not supported for JodisConnection.");
    }

    @Override
    public Object execute(String command, byte[]... args) {
        throw new UnsupportedOperationException("Execute is currently not supported in jodis mode.");
    }

    @Override
    public Long pfAdd(byte[] key, byte[]... values) {
        throw new UnsupportedOperationException("PfAdd is currently not supported for JodisConnection.");
    }

    @Override
    public Long pfCount(byte[]... keys) {
        throw new UnsupportedOperationException("PfCount is currently not supported for JodisConnection.");
    }

    @Override
    public void pfMerge(byte[] destinationKey, byte[]... sourceKeys) {
        throw new UnsupportedOperationException("PfMerge is currently not supported for JodisConnection.");
    }

    @Override
    public void select(int dbIndex) {
        if (dbIndex != 0) {
            throw new InvalidDataAccessApiUsageException("Cannot SELECT non zero index in cluster mode.");
        }
    }

    @Override
    public byte[] echo(byte[] message) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.echo(message))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public String ping() {
        return executor.executeCommandInResourcePool(resourcePool,
                (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) BinaryJedis::ping)
                .getValue().isEmpty() ? "PONG" : null;
    }

    @Override
    public Long geoAdd(byte[] key, Point point, byte[] member) {
        throw new UnsupportedOperationException("GeoAdd is currently not supported for JodisConnection.");
    }

    @Override
    public Long geoAdd(byte[] key, GeoLocation<byte[]> location) {
        throw new UnsupportedOperationException("GeoAdd is currently not supported for JodisConnection.");
    }

    @Override
    public Long geoAdd(byte[] key, Map<byte[], Point> memberCoordinateMap) {
        throw new UnsupportedOperationException("GeoAdd is currently not supported for JodisConnection.");
    }

    @Override
    public Long geoAdd(byte[] key, Iterable<GeoLocation<byte[]>> locations) {
        throw new UnsupportedOperationException("GeoAdd is currently not supported for JodisConnection.");
    }

    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2) {
        throw new UnsupportedOperationException("GeoDist is currently not supported for JodisConnection.");
    }

    @Override
    public Distance geoDist(byte[] key, byte[] member1, byte[] member2, Metric metric) {
        throw new UnsupportedOperationException("GeoDist is currently not supported for JodisConnection.");
    }

    @Override
    public List<String> geoHash(byte[] key, byte[]... members) {
        throw new UnsupportedOperationException("GeoHash is currently not supported for JodisConnection.");
    }

    @Override
    public List<Point> geoPos(byte[] key, byte[]... members) {
        throw new UnsupportedOperationException("GeoPos is currently not supported for JodisConnection.");
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within) {
        throw new UnsupportedOperationException("GeoRadius is currently not supported for JodisConnection.");
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadius(byte[] key, Circle within, GeoRadiusCommandArgs args) {
        throw new UnsupportedOperationException("GeoRadius is currently not supported for JodisConnection.");
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, double radius) {
        throw new UnsupportedOperationException("GeoRadiusByMember is currently not supported for JodisConnection.");
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius) {
        throw new UnsupportedOperationException("GeoRadiusByMember is currently not supported for JodisConnection.");
    }

    @Override
    public GeoResults<GeoLocation<byte[]>> geoRadiusByMember(byte[] key, byte[] member, Distance radius, GeoRadiusCommandArgs args) {
        throw new UnsupportedOperationException("GeoRadiusByMember is currently not supported for JodisConnection.");
    }

    @Override
    public Long geoRemove(byte[] key, byte[]... members) {
        throw new UnsupportedOperationException("GeoRemove is currently not supported for JodisConnection.");
    }

    @Override
    public Boolean hSet(byte[] key, byte[] field, byte[] value) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.hset(key, field, value))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean hSetNX(byte[] key, byte[] field, byte[] value) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.hsetnx(key, field, value))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] hGet(byte[] key, byte[] field) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.hget(key, field))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> hMGet(byte[] key, byte[]... fields) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.hmget(key, fields))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void hMSet(byte[] key, Map<byte[], byte[]> hashes) {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.hmset(key, hashes));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long hIncrBy(byte[] key, byte[] field, long delta) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.hincrBy(key, field, delta))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Double hIncrBy(byte[] key, byte[] field, double delta) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Double>) client ->
                            client.hincrByFloat(key, field, delta))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean hExists(byte[] key, byte[] field) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Boolean>) client ->
                            client.hexists(key, field))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long hDel(byte[] key, byte[]... fields) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.hdel(key, fields))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long hLen(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.hlen(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> hKeys(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.hkeys(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> hVals(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.hvals(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Map<byte[], byte[]> hGetAll(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Map<byte[], byte[]>>) client ->
                            client.hgetAll(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Cursor<Map.Entry<byte[], byte[]>> hScan(byte[] key, ScanOptions options) {

        return new ScanCursor<Map.Entry<byte[], byte[]>>(options) {

            @Override
            protected ScanIteration<Map.Entry<byte[], byte[]>> doScan(long cursorId, ScanOptions options) {

                ScanParams params = JedisConverters.toScanParams(options);

                redis.clients.jedis.ScanResult<Map.Entry<byte[], byte[]>> result = executor
                        .executeCommandInResourcePool(resourcePool,
                                (JodisCommandExecutor.JodisCommandCallback<Jedis, ScanResult<Map.Entry<byte[], byte[]>>>) client ->
                                        client.hscan(key, JedisConverters.toBytes(cursorId), params))
                        .getValue();
                return new ScanIteration<>(Long.parseLong(result.getStringCursor()), result.getResult());
            }
        }.open();
    }

    @Override
    public Boolean exists(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Boolean>) client ->
                            client.exists(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long del(byte[]... keys) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.del(keys))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public DataType type(byte[] key) {
        try {
            return JedisConverters.toDataType(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.type(key))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        throw new UnsupportedOperationException("Keys is currently not supported for JodisConnection.");
    }

    @Override
    public Cursor<byte[]> scan(ScanOptions options) {
        throw new UnsupportedOperationException("Scan is currently not supported for JodisConnection.");
    }

    @Override
    public byte[] randomKey() {
        throw new UnsupportedOperationException("RandomKey is currently not supported for JodisConnection.");
    }

    @Override
    public void rename(byte[] oldName, byte[] newName) {
        throw new UnsupportedOperationException("Rename is currently not supported for JodisConnection.");
    }

    @Override
    public Boolean renameNX(byte[] oldName, byte[] newName) {
        throw new UnsupportedOperationException("RenameNX is currently not supported for JodisConnection.");
    }

    @Override
    public Boolean expire(byte[] key, long seconds) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.expire(key, Long.valueOf(seconds).intValue()))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean pExpire(byte[] key, long millis) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.pexpire(key, millis))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean expireAt(byte[] key, long unixTime) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.expireAt(key, unixTime))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean pExpireAt(byte[] key, long unixTimeInMillis) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.pexpireAt(key, unixTimeInMillis))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean persist(byte[] key) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.persist(key))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean move(byte[] key, int dbIndex) {
        throw new UnsupportedOperationException("Move is currently not supported for JodisConnection.");
    }

    @Override
    public Long ttl(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.ttl(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long ttl(byte[] key, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("Ttl is currently not supported for JodisConnection.");
    }

    @Override
    public Long pTtl(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.pttl(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long pTtl(byte[] key, TimeUnit timeUnit) {
        throw new UnsupportedOperationException("PTtl is currently not supported for JodisConnection.");
    }

    @Override
    public List<byte[]> sort(byte[] key, SortParameters params) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.sort(key, JedisConverters.toSortingParams(params)))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long sort(byte[] key, SortParameters params, byte[] storeKey) {
        List<byte[]> sorted = sort(key, params);
        if (!CollectionUtils.isEmpty(sorted)) {

            byte[][] arr = new byte[sorted.size()][];
            switch (type(key)) {

                case SET:
                    sAdd(storeKey, sorted.toArray(arr));
                    return 1L;
                case LIST:
                    lPush(storeKey, sorted.toArray(arr));
                    return 1L;
                default:
                    throw new IllegalArgumentException("sort and store is only supported for SET and LIST");
            }
        }
        return 0L;
    }

    @Override
    public byte[] dump(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.dump(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void restore(byte[] key, long ttlInMillis, byte[] serializedValue) {
        throw new UnsupportedOperationException("Restore is currently not supported for JodisConnection.");
    }

    @Override
    public Long rPush(byte[] key, byte[]... values) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.rpush(key, values))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long lPush(byte[] key, byte[]... values) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.lpush(key, values))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long rPushX(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.rpushx(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long lPushX(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.lpushx(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long lLen(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.llen(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> lRange(byte[] key, long start, long end) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.lrange(key, start, end))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void lTrim(byte[] key, long start, long end) {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.ltrim(key, start, end));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] lIndex(byte[] key, long index) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.lindex(key, index))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long lInsert(byte[] key, Position where, byte[] pivot, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.linsert(key, JedisConverters.toListPosition(where), pivot, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void lSet(byte[] key, long index, byte[] value) {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.lset(key, index, value));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long lRem(byte[] key, long count, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.lrem(key, count, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] lPop(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.lpop(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] rPop(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.rpop(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> bLPop(int timeout, byte[]... keys) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.blpop(timeout, keys))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> bRPop(int timeout, byte[]... keys) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.brpop(timeout, keys))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] rPopLPush(byte[] srcKey, byte[] dstKey) {
        throw new UnsupportedOperationException("RPopLPush is currently not supported for JodisConnection.");
    }

    @Override
    public byte[] bRPopLPush(int timeout, byte[] srcKey, byte[] dstKey) {
        throw new UnsupportedOperationException("BRPopLPush is currently not supported for JodisConnection.");
    }

    @Override
    public boolean isSubscribed() {
        return (subscription != null && subscription.isAlive());
    }

    @Override
    public Subscription getSubscription() {
        return subscription;
    }

    @Override
    public Long publish(byte[] channel, byte[] message) {
        throw new UnsupportedOperationException("Publish is currently not supported for JodisConnection.");
    }

    @Override
    public void subscribe(MessageListener listener, byte[]... channels) {
        throw new UnsupportedOperationException("Subscribe is currently not supported for JodisConnection.");
    }

    @Override
    public void pSubscribe(MessageListener listener, byte[]... patterns) {
        throw new UnsupportedOperationException("PSubscribe is currently not supported for JodisConnection.");
    }

    @Override
    public void scriptFlush() {
        throw new UnsupportedOperationException("ScriptFlush is currently not supported for JodisConnection.");
    }

    @Override
    public void scriptKill() {
        throw new UnsupportedOperationException("ScriptKill is currently not supported for JodisConnection.");
    }

    @Override
    public String scriptLoad(byte[] script) {
        throw new UnsupportedOperationException("ScriptLoad is currently not supported for JodisConnection.");
    }

    @Override
    public List<Boolean> scriptExists(String... scriptShas) {
        throw new UnsupportedOperationException("ScriptExists is currently not supported for JodisConnection.");
    }

    @Override
    public <T> T eval(byte[] script, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        throw new UnsupportedOperationException("Eval is currently not supported for JodisConnection.");
    }

    @Override
    public <T> T evalSha(String scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        throw new UnsupportedOperationException("EvalSha is currently not supported for JodisConnection.");
    }

    @Override
    public <T> T evalSha(byte[] scriptSha, ReturnType returnType, int numKeys, byte[]... keysAndArgs) {
        throw new UnsupportedOperationException("EvalSha is currently not supported for JodisConnection.");
    }

    @Override
    public void bgWriteAof() {
        throw new UnsupportedOperationException("BgWriteAof is currently not supported for JodisConnection.");
    }

    @Override
    public void bgReWriteAof() {
        throw new UnsupportedOperationException("BgReWriteAof is currently not supported for JodisConnection.");
    }

    @Override
    public void bgSave() {
        throw new UnsupportedOperationException("BgSave is currently not supported for JodisConnection.");
    }

    @Override
    public Long lastSave() {
        throw new UnsupportedOperationException("LastSave is currently not supported for JodisConnection.");
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Save is currently not supported for JodisConnection.");
    }

    @Override
    public Long dbSize() {
        throw new UnsupportedOperationException("DbSize is currently not supported for JodisConnection.");
    }

    @Override
    public void flushDb() {
        throw new UnsupportedOperationException("FlushDb is currently not supported for JodisConnection.");
    }

    @Override
    public void flushAll() {
        throw new UnsupportedOperationException("FlushAll is currently not supported for JodisConnection.");
    }

    @Override
    public Properties info() {
        try {
            return JedisConverters.toProperties(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) BinaryJedis::info)
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Properties info(String section) {
        try {
            return JedisConverters.toProperties(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.info(section))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException("Shutdown is currently not supported for JodisConnection.");
    }

    @Override
    public void shutdown(ShutdownOption option) {
        throw new UnsupportedOperationException("Shutdown is currently not supported for JodisConnection.");
    }

    @Override
    public List<String> getConfig(String pattern) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<String>>) client ->
                            client.configGet(pattern))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void setConfig(String param, String value) {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.configSet(param, value));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void resetConfigStats() {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) BinaryJedis::configResetStat);
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long time() {
        throw new UnsupportedOperationException("Time is currently not supported for JodisConnection.");
    }

    @Override
    public void killClient(String host, int port) {
        final String hostAndPort = String.format("%s:%s", host, port);

        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.clientKill(hostAndPort));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void setClientName(byte[] name) {
        throw new UnsupportedOperationException("SetClientName is currently not supported for JodisConnection.");
    }

    @Override
    public String getClientName() {
        throw new UnsupportedOperationException("GetClientName is currently not supported for JodisConnection.");
    }

    @Override
    public List<RedisClientInfo> getClientList() {
        try {
            String infos = executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) BinaryJedis::clientList)
                    .getValue();
            return new ArrayList<>(JedisConverters.toListOfRedisClientInformation(infos));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }


    }

    @Override
    public void slaveOf(String host, int port) {
        throw new UnsupportedOperationException("SlaveOf is currently not supported for JodisConnection.");
    }

    @Override
    public void slaveOfNoOne() {
        throw new UnsupportedOperationException("SlaveOfNoOne is currently not supported for JodisConnection.");
    }

    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option) {
        throw new UnsupportedOperationException("Migrate is currently not supported for JodisConnection.");
    }

    @Override
    public void migrate(byte[] key, RedisNode target, int dbIndex, MigrateOption option, long timeout) {
        throw new UnsupportedOperationException("Migrate is currently not supported for JodisConnection.");
    }

    @Override
    public Long sAdd(byte[] key, byte[]... values) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.sadd(key, values))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long sRem(byte[] key, byte[]... values) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.srem(key, values))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] sPop(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.spop(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean sMove(byte[] srcKey, byte[] destKey, byte[] value) {
        throw new UnsupportedOperationException("SMove is currently not supported for JodisConnection.");
    }

    @Override
    public Long sCard(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.scard(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean sIsMember(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Boolean>) client ->
                            client.sismember(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> sInter(byte[]... keys) {
        throw new UnsupportedOperationException("SInter is currently not supported for JodisConnection.");
    }

    @Override
    public Long sInterStore(byte[] destKey, byte[]... keys) {
        throw new UnsupportedOperationException("SInterStore is currently not supported for JodisConnection.");
    }

    @Override
    public Set<byte[]> sUnion(byte[]... keys) {
        throw new UnsupportedOperationException("SUnion is currently not supported for JodisConnection.");
    }

    @Override
    public Long sUnionStore(byte[] destKey, byte[]... keys) {
        throw new UnsupportedOperationException("SUnionStore is currently not supported for JodisConnection.");
    }

    @Override
    public Set<byte[]> sDiff(byte[]... keys) {
        throw new UnsupportedOperationException("SDiff is currently not supported for JodisConnection.");
    }

    @Override
    public Long sDiffStore(byte[] destKey, byte[]... keys) {
        throw new UnsupportedOperationException("SDiffStore is currently not supported for JodisConnection.");
    }

    @Override
    public Set<byte[]> sMembers(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.smembers(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] sRandMember(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.srandmember(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> sRandMember(byte[] key, long count) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.srandmember(key, Long.valueOf(count).intValue()))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Cursor<byte[]> sScan(byte[] key, ScanOptions options) {
        throw new UnsupportedOperationException("SScan is currently not supported for JodisConnection.");
    }

    @Override
    public byte[] get(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.get(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] getSet(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.getSet(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public List<byte[]> mGet(byte[]... keys) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, List<byte[]>>) client ->
                            client.mget(keys))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void set(byte[] key, byte[] value) {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.set(key, value));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void set(byte[] key, byte[] value, Expiration expiration, SetOption option) {
        if (expiration == null || expiration.isPersistent()) {

            if (option == null || ObjectUtils.nullSafeEquals(SetOption.UPSERT, option)) {
                set(key, value);
            } else {

                // BinaryCluster does not support set with nxxx and binary key/value pairs.
                if (ObjectUtils.nullSafeEquals(SetOption.SET_IF_PRESENT, option)) {
                    throw new UnsupportedOperationException("Jodis does not support SET XX without PX or EX on BinaryCluster.");
                }

                setNX(key, value);
            }
        } else {

            if (option == null || ObjectUtils.nullSafeEquals(SetOption.UPSERT, option)) {

                if (ObjectUtils.nullSafeEquals(TimeUnit.MILLISECONDS, expiration.getTimeUnit())) {
                    pSetEx(key, expiration.getExpirationTime(), value);
                } else {
                    setEx(key, expiration.getExpirationTime(), value);
                }
            } else {

                byte[] nxxx = JedisConverters.toSetCommandNxXxArgument(option);
                byte[] expx = JedisConverters.toSetCommandExPxArgument(expiration);

                try {
                    executor.executeCommandInResourcePool(resourcePool,
                            (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                                    client.set(key, value, nxxx, expx, expiration.getExpirationTime()));
                } catch (Exception ex) {
                    throw convertJedisAccessException(ex);
                }
            }
        }
    }

    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.setnx(key, value))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void setEx(byte[] key, long seconds, byte[] value) {
        if (seconds > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Seconds have cannot exceed Integer.MAX_VALUE!");
        }

        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.setex(key, Long.valueOf(seconds).intValue(), value));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }

    }

    @Override
    public void pSetEx(byte[] key, long milliseconds, byte[] value) {
        if (milliseconds > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Milliseconds have cannot exceed Integer.MAX_VALUE!");
        }

        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                            client.psetex(key, milliseconds, value));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void mSet(Map<byte[], byte[]> tuples) {
        Assert.notNull(tuples, "Tuples must not be null!");

        if (ClusterSlotHashUtil.isSameSlotForAllKeys(tuples.keySet().toArray(new byte[tuples.keySet().size()][]))) {
            try {
                executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, String>) client ->
                                client.mset(JedisConverters.toByteArrays(tuples)));
                return;
            } catch (Exception ex) {
                throw convertJedisAccessException(ex);
            }
        }

        for (Map.Entry<byte[], byte[]> entry : tuples.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Boolean mSetNX(Map<byte[], byte[]> tuples) {
        Assert.notNull(tuples, "Tuple must not be null!");

        if (ClusterSlotHashUtil.isSameSlotForAllKeys(tuples.keySet().toArray(new byte[tuples.keySet().size()][]))) {
            try {
                return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                                client.msetnx(JedisConverters.toByteArrays(tuples)))
                        .getValue());
            } catch (Exception ex) {
                throw convertJedisAccessException(ex);
            }
        }

        boolean result = true;
        for (Map.Entry<byte[], byte[]> entry : tuples.entrySet()) {
            if (!setNX(entry.getKey(), entry.getValue()) && result) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public Long incr(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.incr(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long incrBy(byte[] key, long value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.incrBy(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Double incrBy(byte[] key, double value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Double>) client ->
                            client.incrByFloat(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long decr(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.decr(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long decrBy(byte[] key, long value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.decrBy(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long append(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.append(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public byte[] getRange(byte[] key, long begin, long end) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, byte[]>) client ->
                            client.getrange(key, begin, end))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void setRange(byte[] key, byte[] value, long offset) {
        try {
            executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.setrange(key, offset, value));
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean getBit(byte[] key, long offset) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Boolean>) client ->
                            client.getbit(key, offset))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Boolean setBit(byte[] key, long offset, boolean value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Boolean>) client ->
                            client.setbit(key, offset, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long bitCount(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.bitcount(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long bitCount(byte[] key, long begin, long end) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.bitcount(key, begin, end))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long bitOp(BitOperation op, byte[] destination, byte[]... keys) {
        throw new UnsupportedOperationException("BitOp is currently not supported for JodisConnection.");
    }

    @Override
    public Long strLen(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.strlen(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public void multi() {
        throw new UnsupportedOperationException("Multi is currently not supported for JodisConnection.");
    }

    @Override
    public List<Object> exec() {
        throw new UnsupportedOperationException("Exec is currently not supported for JodisConnection.");
    }

    @Override
    public void discard() {
        throw new UnsupportedOperationException("Discard is currently not supported for JodisConnection.");
    }

    @Override
    public void watch(byte[]... keys) {
        throw new UnsupportedOperationException("Watch is currently not supported for JodisConnection.");
    }

    @Override
    public void unwatch() {
        throw new UnsupportedOperationException("Unwatch is currently not supported for JodisConnection.");
    }

    @Override
    public Boolean zAdd(byte[] key, double score, byte[] value) {
        try {
            return JedisConverters.toBoolean(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zadd(key, score, value))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zAdd(byte[] key, Set<Tuple> tuples) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zadd(key, JedisConverters.toTupleMap(tuples)))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zRem(byte[] key, byte[]... values) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zrem(key, values))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Double zIncrBy(byte[] key, double increment, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Double>) client ->
                            client.zincrby(key, increment, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zRank(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zrank(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zRevRank(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zrevrank(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRange(byte[] key, long start, long end) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrange(key, start, end))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<Tuple> zRangeWithScores(byte[] key, long start, long end) {
        try {
            return JedisConverters.toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                            client.zrangeWithScores(key, start, end))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, double min, double max) {
        return zRangeByScore(key, new Range().gte(min).lte(max));
    }

    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range) {
        return zRangeByScoreWithScores(key, range, null);
    }

    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, double min, double max) {
        return zRangeByScoreWithScores(key, new Range().gte(min).lte(max));
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, double min, double max, long offset, long count) {
        return zRangeByScore(key, new Range().gte(min).lte(max),
                new Limit().offset(Long.valueOf(offset).intValue()).count(Long.valueOf(count).intValue()));
    }

    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        if (offset > Integer.MAX_VALUE || count > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Count/Offset cannot exceed Integer.MAX_VALUE!");
        }

        try {
            return JedisConverters.toTupleSet(
                    executor.executeCommandInResourcePool(resourcePool,
                            (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                                    client.zrangeByScoreWithScores(key, min, max,
                                            Long.valueOf(offset).intValue(), Long.valueOf(count).intValue()))
                            .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<Tuple> zRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        Assert.notNull(range, "Range cannot be null for ZRANGEBYSCOREWITHSCORES.");

        byte[] min = JedisConverters.boundaryToBytesForZRange(range.getMin(), JedisConverters.NEGATIVE_INFINITY_BYTES);
        byte[] max = JedisConverters.boundaryToBytesForZRange(range.getMax(), JedisConverters.POSITIVE_INFINITY_BYTES);

        try {
            if (limit != null) {
                return JedisConverters
                        .toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                                (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                                        client.zrangeByScoreWithScores(key, min, max,
                                                limit.getOffset(), limit.getCount()))
                                .getValue());
            }
            return JedisConverters.toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                            client.zrangeByScoreWithScores(key, min, max))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRevRange(byte[] key, long start, long end) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrevrange(key, start, end))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<Tuple> zRevRangeWithScores(byte[] key, long start, long end) {
        try {
            return JedisConverters.toTupleSet(
                    executor.executeCommandInResourcePool(resourcePool,
                            (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                                    client.zrevrangeWithScores(key, start, end))
                            .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, double min, double max) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrevrangeByScore(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, Range range) {
        return zRevRangeByScore(key, range, null);
    }

    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max) {
        try {
            return JedisConverters.toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                            client.zrevrangeByScoreWithScores(key, min, max))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, double min, double max, long offset, long count) {
        if (offset > Integer.MAX_VALUE || count > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Count/Offset cannot exceed Integer.MAX_VALUE!");
        }

        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrevrangeByScore(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }

    }

    @Override
    public Set<byte[]> zRevRangeByScore(byte[] key, Range range, Limit limit) {
        Assert.notNull(range, "Range cannot be null for ZREVRANGEBYSCORE.");
        byte[] min = JedisConverters.boundaryToBytesForZRange(range.getMin(), JedisConverters.NEGATIVE_INFINITY_BYTES);
        byte[] max = JedisConverters.boundaryToBytesForZRange(range.getMax(), JedisConverters.POSITIVE_INFINITY_BYTES);

        try {
            if (limit != null) {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                                client.zrevrangeByScore(key, min, max, limit.getOffset(), limit.getCount()))
                        .getValue();
            }
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrevrangeByScore(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }

    }

    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, double min, double max, long offset, long count) {
        if (offset > Integer.MAX_VALUE || count > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Count/Offset cannot exceed Integer.MAX_VALUE!");
        }

        try {
            return JedisConverters.toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                            client.zrevrangeByScoreWithScores(key, min, max,
                                    Long.valueOf(offset).intValue(), Long.valueOf(count).intValue()))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }

    }

    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range) {
        return zRevRangeByScoreWithScores(key, range, null);
    }

    @Override
    public Set<Tuple> zRevRangeByScoreWithScores(byte[] key, Range range, Limit limit) {
        Assert.notNull(range, "Range cannot be null for ZREVRANGEBYSCOREWITHSCORES.");

        byte[] min = JedisConverters.boundaryToBytesForZRange(range.getMin(), JedisConverters.NEGATIVE_INFINITY_BYTES);
        byte[] max = JedisConverters.boundaryToBytesForZRange(range.getMax(), JedisConverters.POSITIVE_INFINITY_BYTES);

        try {
            if (limit != null) {
                return JedisConverters.toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                                client.zrevrangeByScoreWithScores(key, min, max,
                                        limit.getOffset(), limit.getCount()))
                        .getValue());
            }
            return JedisConverters.toTupleSet(executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<redis.clients.jedis.Tuple>>) client ->
                            client.zrevrangeByScoreWithScores(key, min, max))
                    .getValue());
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zCount(byte[] key, double min, double max) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zcount(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zCount(byte[] key, Range range) {
        Assert.notNull(range, "Range cannot be null for ZCOUNT.");

        byte[] min = JedisConverters.boundaryToBytesForZRange(range.getMin(), JedisConverters.NEGATIVE_INFINITY_BYTES);
        byte[] max = JedisConverters.boundaryToBytesForZRange(range.getMax(), JedisConverters.POSITIVE_INFINITY_BYTES);

        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zcount(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zCard(byte[] key) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zcard(key))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Double zScore(byte[] key, byte[] value) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Double>) client ->
                            client.zscore(key, value))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zRemRange(byte[] key, long start, long end) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zremrangeByRank(key, start, end))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zRemRangeByScore(byte[] key, double min, double max) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zremrangeByScore(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zRemRangeByScore(byte[] key, Range range) {
        Assert.notNull(range, "Range cannot be null for ZREMRANGEBYSCORE.");

        byte[] min = JedisConverters.boundaryToBytesForZRange(range.getMin(), JedisConverters.NEGATIVE_INFINITY_BYTES);
        byte[] max = JedisConverters.boundaryToBytesForZRange(range.getMax(), JedisConverters.POSITIVE_INFINITY_BYTES);

        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                            client.zremrangeByScore(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Long zUnionStore(byte[] destKey, byte[]... sets) {
        byte[][] allKeys = ByteUtils.mergeArrays(destKey, sets);

        if (ClusterSlotHashUtil.isSameSlotForAllKeys(allKeys)) {

            try {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                                client.zunionstore(destKey, sets))
                        .getValue();
            } catch (Exception ex) {
                throw convertJedisAccessException(ex);
            }
        }

        throw new InvalidDataAccessApiUsageException("ZUNIONSTORE can only be executed when all keys map to the same slot");
    }

    @Override
    public Long zUnionStore(byte[] destKey, Aggregate aggregate, int[] weights, byte[]... sets) {
        byte[][] allKeys = ByteUtils.mergeArrays(destKey, sets);

        if (ClusterSlotHashUtil.isSameSlotForAllKeys(allKeys)) {

            //noinspection deprecation
            ZParams zparams = new ZParams().weights(weights).aggregate(ZParams.Aggregate.valueOf(aggregate.name()));

            try {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                                client.zunionstore(destKey, zparams, sets))
                        .getValue();
            } catch (Exception ex) {
                throw convertJedisAccessException(ex);
            }
        }

        throw new InvalidDataAccessApiUsageException("ZUNIONSTORE can only be executed when all keys map to the same slot");
    }

    @Override
    public Long zInterStore(byte[] destKey, byte[]... sets) {
        byte[][] allKeys = ByteUtils.mergeArrays(destKey, sets);

        if (ClusterSlotHashUtil.isSameSlotForAllKeys(allKeys)) {

            try {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                                client.zinterstore(destKey, sets))
                        .getValue();
            } catch (Exception ex) {
                throw convertJedisAccessException(ex);
            }
        }

        throw new InvalidDataAccessApiUsageException("ZINTERSTORE can only be executed when all keys map to the same slot");
    }

    @Override
    public Long zInterStore(byte[] destKey, Aggregate aggregate, int[] weights, byte[]... sets) {
        byte[][] allKeys = ByteUtils.mergeArrays(destKey, sets);

        if (ClusterSlotHashUtil.isSameSlotForAllKeys(allKeys)) {

            //noinspection deprecation
            ZParams zparams = new ZParams().weights(weights).aggregate(ZParams.Aggregate.valueOf(aggregate.name()));

            try {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Long>) client ->
                                client.zinterstore(destKey, zparams, sets))
                        .getValue();
            } catch (Exception ex) {
                throw convertJedisAccessException(ex);
            }
        }

        throw new IllegalArgumentException("ZINTERSTORE can only be executed when all keys map to the same slot");
    }

    @Override
    public Cursor<Tuple> zScan(byte[] key, ScanOptions options) {
        return new ScanCursor<Tuple>(options) {

            @Override
            protected ScanIteration<Tuple> doScan(long cursorId, ScanOptions options) {

                ScanParams params = JedisConverters.toScanParams(options);

                redis.clients.jedis.ScanResult<redis.clients.jedis.Tuple> result =
                        executor.executeCommandInResourcePool(resourcePool,
                                (JodisCommandExecutor.JodisCommandCallback<Jedis, ScanResult<redis.clients.jedis.Tuple>>) client ->
                                        client.zscan(key, JedisConverters.toBytes(cursorId), params))
                                .getValue();

                return new ScanIteration<>(Long.parseLong(result.getStringCursor()),
                        JedisConverters.tuplesToTuples().convert(result.getResult()));
            }
        }.open();
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max) {
        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrangeByScore(key, JedisConverters.toBytes(min), JedisConverters.toBytes(max)))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, Range range) {
        return zRangeByScore(key, range, null);
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, String min, String max, long offset, long count) {
        if (offset > Integer.MAX_VALUE || count > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Count/Offset cannot exceed Integer.MAX_VALUE!");
        }

        try {
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrangeByScore(key, JedisConverters.toBytes(min), JedisConverters.toBytes(max),
                                    Long.valueOf(offset).intValue(), Long.valueOf(count).intValue()))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRangeByScore(byte[] key, Range range, Limit limit) {
        Assert.notNull(range, "Range cannot be null for ZRANGEBYSCORE.");

        byte[] min = JedisConverters.boundaryToBytesForZRange(range.getMin(), JedisConverters.NEGATIVE_INFINITY_BYTES);
        byte[] max = JedisConverters.boundaryToBytesForZRange(range.getMax(), JedisConverters.POSITIVE_INFINITY_BYTES);

        try {
            if (limit != null) {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                                client.zrangeByScore(key, min, max,
                                        limit.getOffset(), limit.getCount()))
                        .getValue();
            }
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrangeByScore(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    @Override
    public Set<byte[]> zRangeByLex(byte[] key) {
        return zRangeByLex(key, Range.unbounded());
    }

    @Override
    public Set<byte[]> zRangeByLex(byte[] key, Range range) {
        return zRangeByLex(key, range, null);
    }

    @Override
    public Set<byte[]> zRangeByLex(byte[] key, Range range, Limit limit) {
        Assert.notNull(range, "Range cannot be null for ZRANGEBYLEX.");

        byte[] min = JedisConverters.boundaryToBytesForZRangeByLex(range.getMin(), JedisConverters.toBytes("-"));
        byte[] max = JedisConverters.boundaryToBytesForZRangeByLex(range.getMax(), JedisConverters.toBytes("+"));

        try {
            if (limit != null) {
                return executor.executeCommandInResourcePool(resourcePool,
                        (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                                client.zrangeByLex(key, min, max,
                                        limit.getOffset(), limit.getCount()))
                        .getValue();
            }
            return executor.executeCommandInResourcePool(resourcePool,
                    (JodisCommandExecutor.JodisCommandCallback<Jedis, Set<byte[]>>) client ->
                            client.zrangeByLex(key, min, max))
                    .getValue();
        } catch (Exception ex) {
            throw convertJedisAccessException(ex);
        }
    }

    protected DataAccessException convertJedisAccessException(Exception ex) {

        DataAccessException translated = EXCEPTION_TRANSLATION.translate(ex);
        return translated != null ? translated : new RedisSystemException(ex.getMessage(), ex);
    }

    // RedisClusterConnection method

    @Override
    public String ping(RedisClusterNode node) {
        throw new UnsupportedOperationException("Ping is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void bgReWriteAof(RedisClusterNode node) {
        throw new UnsupportedOperationException("BgReWriteAof is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void bgSave(RedisClusterNode node) {
        throw new UnsupportedOperationException("BgSave is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Long lastSave(RedisClusterNode node) {
        throw new UnsupportedOperationException("LastSave is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void save(RedisClusterNode node) {
        throw new UnsupportedOperationException("Save is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Long dbSize(RedisClusterNode node) {
        throw new UnsupportedOperationException("DbSize is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void flushDb(RedisClusterNode node) {
        throw new UnsupportedOperationException("FlushDb is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void flushAll(RedisClusterNode node) {
        throw new UnsupportedOperationException("FlushAll is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Properties info(RedisClusterNode node) {
        throw new UnsupportedOperationException("Info is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Properties info(RedisClusterNode node, String section) {
        throw new UnsupportedOperationException("Info is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Set<byte[]> keys(RedisClusterNode node, byte[] pattern) {
        throw new UnsupportedOperationException("Keys is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public byte[] randomKey(RedisClusterNode node) {
        throw new UnsupportedOperationException("RandomKey is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void shutdown(RedisClusterNode node) {
        throw new UnsupportedOperationException("Shutdown is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public List<String> getConfig(RedisClusterNode node, String pattern) {
        throw new UnsupportedOperationException("GetConfig is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void setConfig(RedisClusterNode node, String param, String value) {
        throw new UnsupportedOperationException("SetConfig is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void resetConfigStats(RedisClusterNode node) {
        throw new UnsupportedOperationException("ResetConfigStats is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Long time(RedisClusterNode node) {
        throw new UnsupportedOperationException("Time is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public List<RedisClientInfo> getClientList(RedisClusterNode node) {
        throw new UnsupportedOperationException("GetClientList is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Iterable<RedisClusterNode> clusterGetNodes() {
        throw new UnsupportedOperationException("ClusterGetNodes is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Collection<RedisClusterNode> clusterGetSlaves(RedisClusterNode master) {
        throw new UnsupportedOperationException("ClusterGetSlaves is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Map<RedisClusterNode, Collection<RedisClusterNode>> clusterGetMasterSlaveMap() {
        throw new UnsupportedOperationException("ClusterGetMasterSlaveMap is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Integer clusterGetSlotForKey(byte[] key) {
        throw new UnsupportedOperationException("ClusterGetSlotForKey is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public RedisClusterNode clusterGetNodeForSlot(int slot) {
        throw new UnsupportedOperationException("ClusterGetNodeForSlot is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public RedisClusterNode clusterGetNodeForKey(byte[] key) {
        throw new UnsupportedOperationException("ClusterGetNodeForKey is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public ClusterInfo clusterGetClusterInfo() {
        throw new UnsupportedOperationException("ClusterGetClusterInfo is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterAddSlots(RedisClusterNode node, int... slots) {
        throw new UnsupportedOperationException("ClusterAddSlots is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterAddSlots(RedisClusterNode node, RedisClusterNode.SlotRange range) {
        throw new UnsupportedOperationException("ClusterAddSlots is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public Long clusterCountKeysInSlot(int slot) {
        throw new UnsupportedOperationException("ClusterCountKeysInSlot is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterDeleteSlots(RedisClusterNode node, int... slots) {
        throw new UnsupportedOperationException("ClusterDeleteSlots is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterDeleteSlotsInRange(RedisClusterNode node, RedisClusterNode.SlotRange range) {
        throw new UnsupportedOperationException("ClusterDeleteSlotsInRange is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterForget(RedisClusterNode node) {
        throw new UnsupportedOperationException("ClusterForget is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterMeet(RedisClusterNode node) {
        throw new UnsupportedOperationException("ClusterMeet is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterSetSlot(RedisClusterNode node, int slot, AddSlots mode) {
        throw new UnsupportedOperationException("ClusterSetSlot is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public List<byte[]> clusterGetKeysInSlot(int slot, Integer count) {
        throw new UnsupportedOperationException("ClusterGetKeysInSlot is currently not supported for JodisConnection with cluster mode.");
    }

    @Override
    public void clusterReplicate(RedisClusterNode master, RedisClusterNode slave) {
        throw new UnsupportedOperationException("ClusterReplicate is currently not supported for JodisConnection with cluster mode.");
    }
}
