package net.josscoder.redisbridge.nukkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import net.josscoder.redisbridge.core.RedisBridgeCore;
import net.josscoder.redisbridge.core.data.InstanceInfo;
import net.josscoder.redisbridge.nukkit.command.LobbyCommand;
import net.josscoder.redisbridge.nukkit.command.TransferCommand;
import net.josscoder.redisbridge.nukkit.logger.Logger;

import java.util.Arrays;

public class RedisBridgePlugin extends PluginBase {

    @Getter
    private static RedisBridgePlugin instance;

    @Getter
    private RedisBridgeCore core;

    @Getter
    private InstanceInfo currentInstanceInfo;

    private TaskHandler heartbeatTask;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupCore();
        setupInstance();

        getServer().getCommandMap().registerAll("redisbridge", Arrays.asList(new LobbyCommand(), new TransferCommand()));

        scheduleInstanceHeartbeatTask();

        getLogger().info(TextFormat.GREEN + "RedisBridge Plugin Enabled");
    }

    private void setupCore() {
        Config config = getConfig();

        core = new RedisBridgeCore();
        core.connect(
                config.getString("redis.host"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                new Logger()
        );
    }

    private void setupInstance() {
        Config config = getConfig();

        currentInstanceInfo = new InstanceInfo(
                config.getString("instance.id"),
                config.getString("instance.host"),
                getServer().getPort(),
                config.getString("instance.group"),
                getServer().getMaxPlayers()
        );
    }

    private void scheduleInstanceHeartbeatTask() {
        heartbeatTask = getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            currentInstanceInfo.setPlayers(getServer().getOnlinePlayers().size());
            core.publishInstanceInfo(currentInstanceInfo);
        }, 20);
    }

    @Override
    public void onDisable() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
        }

        core.publishInstanceRemove(currentInstanceInfo);

        if (core != null) {
            core.close();
        }

        getLogger().info(TextFormat.RED + "RedisBridge Plugin Disabled");
    }
}
