package net.josscoder.redisbridge.core.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.josscoder.redisbridge.core.message.MessageBase;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(MessageBase obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String extractType(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return node.get("type").asText();
        } catch (Exception e) {
            throw new RuntimeException("Could not extract type", e);
        }
    }
}
