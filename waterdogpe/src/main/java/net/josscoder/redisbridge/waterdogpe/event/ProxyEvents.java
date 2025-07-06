package net.josscoder.redisbridge.waterdogpe.event;

import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.event.defaults.ServerTransferRequestEvent;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.josscoder.redisbridge.core.manager.InstanceManager;

public class ProxyEvents {

    public static void onPing(ProxyPingEvent event) {
        event.setMaximumPlayerCount(InstanceManager.getInstance().getTotalMaxPlayers());
    }

    public static void onQuery(ProxyQueryEvent event) {
        event.setMaximumPlayerCount(InstanceManager.getInstance().getTotalMaxPlayers());
    }

    public static void onTransferRequest(ServerTransferRequestEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ServerInfo currentServer = player.getServerInfo();
        ServerInfo targetServer = event.getTargetServer();

        if (currentServer == null || targetServer == null || currentServer.getServerName().equalsIgnoreCase(targetServer.getServerName())) {
            return;
        }

        player.sendMessage(Color.GRAY + "Connecting you to " + Color.WHITE + targetServer.getServerName() + Color.GRAY + "!");
    }
}
