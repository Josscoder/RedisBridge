package net.josscoder.redisbridge.nukkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;

public class RedisBridgePlugin extends PluginBase {

    @Getter
    private static RedisBridgePlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info(TextFormat.GREEN + "RedisBridge Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "RedisBridge Plugin Disabled");
    }
}
