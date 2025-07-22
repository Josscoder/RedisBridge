package net.josscoder.redisbridge.waterdogpe.handler;

import dev.waterdog.waterdogpe.network.connection.handler.IJoinHandler;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.instance.InstanceManager;
import net.josscoder.redisbridge.waterdogpe.utils.Utils;

public class LobbyJoinHandler implements IJoinHandler {

    @Override
    public ServerInfo determineServer(ProxiedPlayer proxiedPlayer) {
        InstanceInfo availableInstance = InstanceManager.getInstance().selectAvailableInstance(
                "lobby",
                InstanceManager.SelectionStrategy.LOWEST_PLAYERS
        );
        if (availableInstance == null) {
            return null;
        }

        return Utils.instanceInfoToBedrockServerInfo(availableInstance);
    }
}
