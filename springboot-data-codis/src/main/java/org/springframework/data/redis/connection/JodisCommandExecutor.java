package org.springframework.data.redis.connection;

import io.codis.jodis.JedisResourcePool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.util.ByteArrayWrapper;
import redis.clients.jedis.Jedis;

@Slf4j
public class JodisCommandExecutor implements DisposableBean {

    private final JodisResourceProvider resourceProvider;

    public JodisCommandExecutor() {
        resourceProvider = new JodisResourceProvider() {
            @Override
            public <S> S getResource(JedisResourcePool resourcePool) {
                //noinspection unchecked
                return (S) resourcePool.getResource();
            }

            @Override
            public void returnResource(Jedis resource) {
                resource.close();
            }
        };
    }

    public <S, T> NodeResult<T> executeCommandInResourcePool(JedisResourcePool resourcePool, JodisCommandCallback<S, T> command) {
        S resource = resourceProvider.getResource(resourcePool);
        try {
            return new NodeResult<>(command.doInJodis(resource));
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            resourceProvider.returnResource((Jedis) resource);
        }
    }


    @Override
    public void destroy() throws Exception {

    }


    public interface JodisCommandCallback<T, S> {
        S doInJodis(T client);
    }


    public static class NodeResult<T> {
        @Getter
        private T value;
        private ByteArrayWrapper key;

        public NodeResult(T value) {
            this(value, new byte[]{});
        }

        public NodeResult(T value, byte[] key) {
            this.value = value;
            this.key = new ByteArrayWrapper(key);
        }


        public byte[] getKey() {
            return key.getArray();
        }
    }
}
