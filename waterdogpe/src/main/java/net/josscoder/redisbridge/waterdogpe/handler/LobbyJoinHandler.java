package net.josscoder.redisbridge.waterdogpe.handler;

import dev.waterdog.waterdogpe.network.connection.handler.IJoinHandler;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.josscoder.redisbridge.core.data.InstanceInfo;
import net.josscoder.redisbridge.core.manager.InstanceManager;
import net.josscoder.redisbridge.waterdogpe.utils.Utils;

public class LobbyJoinHandler implements IJoinHandler {

    @Override
    public ServerInfo determineServer(ProxiedPlayer proxiedPlayer) {
        InstanceInfo availableInstance = InstanceManager.getInstance().selectAvailableInstance(
                "lobby",
                InstanceManager.SelectionStrategy.MOST_PLAYERS_AVAILABLE
        );
        if (availableInstance == null) {
            return null;
        }

        return Utils.instanceInfoToBedrockServerInfo(availableInstance);
    }
}
