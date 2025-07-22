package net.josscoder.redisbridge.core.message.defaults;

import net.josscoder.redisbridge.core.message.MessageBase;

public class InstanceShutdownMessage extends MessageBase {

    public static final String TYPE = "instance_shutdown";

    public InstanceShutdownMessage() {
        super(TYPE);
    }
}
