package net.josscoder.redisbridge.waterdogpe;

import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.plugin.Plugin;
import lombok.Getter;

public class RedisBridgePlugin extends Plugin {

    @Getter
    private static RedisBridgePlugin instance;

    @Override
    public void onStartup() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("{}RedisBridge Plugin Enabled", Color.GREEN);
    }

    @Override
    public void onDisable() {
        getLogger().info("{}RedisBridge Plugin Disabled", Color.RED);
    }
}
