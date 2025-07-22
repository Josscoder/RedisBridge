package net.josscoder.redisbridge.core.message;

import lombok.Data;

@Data
public abstract class MessageBase {

    private final String type;
    private String sender;
    private Long timestamp;
}
