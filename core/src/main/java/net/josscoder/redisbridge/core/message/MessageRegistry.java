package net.josscoder.redisbridge.core.message;

import java.util.HashMap;
import java.util.Map;

public class MessageRegistry {

    private static final Map<String, Class<? extends MessageBase>> messages = new HashMap<>();

    public static void register(String type, Class<? extends MessageBase> aClass) {
        messages.put(type, aClass);
    }

    public static Class<? extends MessageBase> getClass(String type) {
        return messages.get(type);
    }
}
