package net.josscoder.redisbridge.waterdogpe.task;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.scheduler.Task;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.instance.InstanceManager;
import net.josscoder.redisbridge.waterdogpe.RedisBridgePlugin;
import net.josscoder.redisbridge.waterdogpe.utils.Utils;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class InstanceRegistrationTask extends Task {

    @Override
    public void onRun(int i) {
        ProxyServer server = ProxyServer.getInstance();
        Logger logger = RedisBridgePlugin.getInstance().getLogger();
        Map<String, InstanceInfo> instances = InstanceManager.getInstance().map();

        server.getServers().forEach(serverInfo -> {
            InstanceInfo instanceInfo = instances.get(serverInfo.getServerName());
            if (instanceInfo == null) {
                server.removeServerInfo(serverInfo.getServerName());
                logger.info("Server '%s' has been removed due to timeout or broken connection.".formatted(serverInfo.getServerName()));

                return;
            }

            if (!serverInfo.matchAddress(instanceInfo.getHost(), instanceInfo.getPort())) {
                server.removeServerInfo(serverInfo.getServerName());
                server.registerServerInfo(Utils.instanceInfoToBedrockServerInfo(instanceInfo));
                logger.info("Server '%s' address or port has been updated to %s:%s".formatted(
                        serverInfo.getServerName(),
                        instanceInfo.getHost(),
                        instanceInfo.getPort()
                ));
            }
        });

        instances.entrySet().stream().filter(entry -> server.getServerInfo(entry.getKey()) == null).forEach(entry -> {
            InstanceInfo instanceInfo = entry.getValue();
            server.registerServerInfo(Utils.instanceInfoToBedrockServerInfo(instanceInfo));
            logger.info("New server '%s' registered with address %s:%s".formatted(
                    instanceInfo.getId(),
                    instanceInfo.getHost(),
                    instanceInfo.getPort()
            ));
        });
    }

    @Override
    public void onCancel() {

    }
}
