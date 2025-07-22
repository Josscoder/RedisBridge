package net.josscoder.redisbridge.core.message.defaults;

import lombok.Getter;
import lombok.Setter;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.message.MessageBase;

@Setter
@Getter
public class InstanceHeartbeatMessage extends MessageBase {

    public static final String TYPE = "instance_heartbeat";

    private InstanceInfo instance;

    public InstanceHeartbeatMessage() {
        super(TYPE);
    }
}
