package net.josscoder.redisbridge.nukkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import net.josscoder.redisbridge.nukkit.command.LobbyCommand;
import net.josscoder.redisbridge.nukkit.command.TransferCommand;

import java.util.Arrays;

public class RedisBridgePlugin extends PluginBase {

    @Getter
    private static RedisBridgePlugin instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getCommandMap().registerAll("redisbridge", Arrays.asList(new LobbyCommand(), new TransferCommand()));

        getLogger().info(TextFormat.GREEN + "RedisBridge Plugin Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info(TextFormat.RED + "RedisBridge Plugin Disabled");
    }
}
