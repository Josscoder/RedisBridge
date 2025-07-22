package net.josscoder.redisbridge.waterdogpe.utils;

import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import net.josscoder.redisbridge.core.instance.InstanceInfo;

import java.net.InetSocketAddress;

public class Utils {

    public static BedrockServerInfo instanceInfoToBedrockServerInfo(InstanceInfo instanceInfo) {
        return new BedrockServerInfo(
                instanceInfo.getId(),
                new InetSocketAddress(instanceInfo.getHost(), instanceInfo.getPort()),
                null
        );
    }
}
