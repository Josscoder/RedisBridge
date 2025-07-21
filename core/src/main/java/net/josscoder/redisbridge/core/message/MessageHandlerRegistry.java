package net.josscoder.redisbridge.core.message;

import java.util.HashMap;
import java.util.Map;

public class MessageHandlerRegistry {

    private static final Map<String, MessageHandler<? extends MessageBase>> handlers = new HashMap<>();

    public static void register(String type, MessageHandler<? extends MessageBase> handler) {
        handlers.put(type, handler);
    }

    @SuppressWarnings("unchecked")
    public static <T extends MessageBase> MessageHandler<T> getHandler(String type) {
        return (MessageHandler<T>) handlers.get(type);
    }
}
