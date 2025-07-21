package net.josscoder.redisbridge.nukkit;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import net.josscoder.redisbridge.core.RedisBridgeCore;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.message.defaults.InstanceHeartbeatMessage;
import net.josscoder.redisbridge.core.message.defaults.InstanceShutdownMessage;
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
    private InstanceInfo instanceInfo;

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
        core.registerDefaultMessages();
    }

    private void setupInstance() {
        Config config = getConfig();

        instanceInfo = new InstanceInfo();
        instanceInfo.setId(config.getString("instance.id"));
        instanceInfo.setHost(config.getString("instance.host"));
        instanceInfo.setPort(getServer().getPort());
        instanceInfo.setGroup(config.getString("instance.group"));
        instanceInfo.setMaxPlayers(getServer().getMaxPlayers());
    }

    private void scheduleInstanceHeartbeatTask() {
        heartbeatTask = getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            instanceInfo.setPlayers(getServer().getOnlinePlayers().size());

            InstanceHeartbeatMessage message = new InstanceHeartbeatMessage();
            message.setInstance(instanceInfo);

            core.publish(message, instanceInfo.getId());
        }, 20);
    }

    @Override
    public void onDisable() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel();
        }

        InstanceShutdownMessage message = new InstanceShutdownMessage();
        core.publish(message, instanceInfo.getId());

        if (core != null) {
            core.close();
        }

        getLogger().info(TextFormat.RED + "RedisBridge Plugin Disabled");
    }
}
