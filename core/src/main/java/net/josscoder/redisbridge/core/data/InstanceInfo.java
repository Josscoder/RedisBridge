package net.josscoder.redisbridge.core.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class InstanceInfo {

    private final String id;
    private final String host;
    private final int port;
    private final String group;
    private final int maxPlayers;
    private int players;

    public boolean isFull() {
        return players >= maxPlayers;
    }
}
