package net.josscoder.redisbridge.core.message;

public abstract class MessageHandler <T extends MessageBase> {
    public abstract void handle(T message);
}
