package net.josscoder.redisbridge.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.josscoder.redisbridge.core.data.InstanceInfo;
import net.josscoder.redisbridge.core.logger.ILogger;
import net.josscoder.redisbridge.core.manager.InstanceManager;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisBridgeCore {

    private static final Gson GSON = new GsonBuilder().create();
    public static final String INSTANCE_HEARTBEAT_CHANNEL = "instance_heartbeat_channel";
    public static final String INSTANCE_REMOVE_CHANNEL = "instance_removed_channel";

    private JedisPool jedisPool = null;
    private Thread listenerThread;

    public void connect(String host, int port, String password, ILogger logger) {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(RedisBridgeCore.class.getClassLoader());

        int timeout = 5000;
        if (password != null && !password.trim().isEmpty()) {
            jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), host, port, timeout, password);
        } else {
            jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), host, port, timeout);
        }

        Thread.currentThread().setContextClassLoader(previous);

        listenerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !jedisPool.isClosed()) {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            if (channel.equals(INSTANCE_HEARTBEAT_CHANNEL)) {
                                InstanceInfo data = GSON.fromJson(message, InstanceInfo.class);
                                InstanceManager.INSTANCE_CACHE.put(data.getId(), data);
                            } else if (channel.equals(INSTANCE_REMOVE_CHANNEL)) {
                                InstanceInfo data = GSON.fromJson(message, InstanceInfo.class);
                                InstanceManager.INSTANCE_CACHE.invalidate(data.getId());
                            }
                        }
                    }, INSTANCE_REMOVE_CHANNEL, INSTANCE_HEARTBEAT_CHANNEL);
                } catch (Exception e) {
                    logger.error("RedisBridge encountered an error, will retry in 1 second", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }, "RedisBridge");

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void publish(String message, String channel) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channel, message);
        }
    }

    public void publishInstanceInfo(InstanceInfo info) {
        publish(GSON.toJson(info), INSTANCE_HEARTBEAT_CHANNEL);
    }

    public void publishInstanceRemove(InstanceInfo info) {
        publish(GSON.toJson(info), INSTANCE_REMOVE_CHANNEL);
    }

    public void close() {
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}