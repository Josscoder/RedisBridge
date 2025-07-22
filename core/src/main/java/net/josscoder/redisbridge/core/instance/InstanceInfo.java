package net.josscoder.redisbridge.core.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceInfo {
    private String id;
    private String host;
    private int port;
    private String group;
    private int maxPlayers;
    private int players;

    public boolean isFull() {
        return players >= maxPlayers;
    }
}
