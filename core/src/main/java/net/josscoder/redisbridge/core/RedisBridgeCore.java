package net.josscoder.redisbridge.core;

import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.instance.InstanceManager;
import net.josscoder.redisbridge.core.message.MessageBase;
import net.josscoder.redisbridge.core.message.MessageHandler;
import net.josscoder.redisbridge.core.message.MessageHandlerRegistry;
import net.josscoder.redisbridge.core.message.MessageRegistry;
import net.josscoder.redisbridge.core.logger.ILogger;
import net.josscoder.redisbridge.core.message.defaults.InstanceHeartbeatMessage;
import net.josscoder.redisbridge.core.message.defaults.InstanceShutdownMessage;
import net.josscoder.redisbridge.core.utils.JsonUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class RedisBridgeCore {

    public static final String CHANNEL = "redis-bridge-channel";

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
                        public void onMessage(String channel, String messageJson) {
                            try {
                                String type = JsonUtils.extractType(messageJson);

                                Class<? extends MessageBase> clazz = MessageRegistry.getClass(type);
                                if (clazz == null) {
                                    logger.debug("Unregistered message type: " + type);

                                    return;
                                }

                                MessageBase message = JsonUtils.fromJson(messageJson, clazz);

                                MessageHandler<MessageBase> handler = MessageHandlerRegistry.getHandler(type);
                                if (handler != null) {
                                    handler.handle(message);
                                } else {
                                    logger.debug("No handler found for message type: " + type);
                                }
                            } catch (Exception e) {
                                logger.error("Error handling message", e);
                            }
                        }
                    }, CHANNEL);
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

    public void publish(MessageBase message, String sender) {
        try (Jedis jedis = jedisPool.getResource()) {
            message.setTimestamp(System.currentTimeMillis());
            message.setSender(sender);

            String json = JsonUtils.toJson(message);
            jedis.publish(CHANNEL, json);
        }
    }

    public void registerDefaultMessages() {
        MessageRegistry.register(InstanceHeartbeatMessage.TYPE, InstanceHeartbeatMessage.class);
        MessageHandlerRegistry.register(InstanceHeartbeatMessage.TYPE, new MessageHandler<InstanceHeartbeatMessage>() {
            @Override
            public void handle(InstanceHeartbeatMessage message) {
                InstanceInfo instance = message.getInstance();
                InstanceManager.INSTANCE_CACHE.put(instance.getId(), instance);
            }
        });

        MessageRegistry.register(InstanceShutdownMessage.TYPE, InstanceShutdownMessage.class);
        MessageHandlerRegistry.register(InstanceShutdownMessage.TYPE, new MessageHandler<InstanceShutdownMessage>() {
            @Override
            public void handle(InstanceShutdownMessage message) {
                InstanceManager.INSTANCE_CACHE.invalidate(message.getSender());
            }
        });
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