package net.josscoder.redisbridge.waterdogpe.handler;

import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.connection.handler.IReconnectHandler;
import dev.waterdog.waterdogpe.network.connection.handler.ReconnectReason;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.instance.InstanceManager;
import net.josscoder.redisbridge.waterdogpe.RedisBridgePlugin;
import net.josscoder.redisbridge.waterdogpe.utils.Utils;

public class ReconnectLobbyHandler implements IReconnectHandler {

    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer player, ServerInfo oldServer, ReconnectReason reason, String kickMessage) {
        String oldServerName = oldServer == null ? "?" : oldServer.getServerName();
        String proxyId = RedisBridgePlugin.getInstance().getProxyId();
        player.sendMessage(Color.GRAY + "\n\nUnexpectedly disconnected from " + Color.WHITE + proxyId + "-" + oldServerName +
                Color.GRAY + ". Redirecting to a fallback server...\n" +
                Color.RED + "If this issue persists, please contact the server administrator.\n\n"
        );

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
