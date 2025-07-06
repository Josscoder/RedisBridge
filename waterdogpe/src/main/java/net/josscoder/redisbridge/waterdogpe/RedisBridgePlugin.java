package net.josscoder.redisbridge.waterdogpe;

import dev.waterdog.waterdogpe.command.CommandMap;
import dev.waterdog.waterdogpe.event.EventManager;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.event.defaults.ServerTransferRequestEvent;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import net.josscoder.redisbridge.core.RedisBridgeCore;
import net.josscoder.redisbridge.waterdogpe.command.WhereAmICommand;
import net.josscoder.redisbridge.waterdogpe.event.ProxyEvents;
import net.josscoder.redisbridge.waterdogpe.handler.LobbyJoinHandler;
import net.josscoder.redisbridge.waterdogpe.handler.ReconnectLobbyHandler;
import net.josscoder.redisbridge.waterdogpe.logger.Logger;
import net.josscoder.redisbridge.waterdogpe.task.InstanceRegistrationTask;

public class RedisBridgePlugin extends Plugin {

    @Getter
    private static RedisBridgePlugin instance;

    @Getter
    private String proxyId;

    @Getter
    private RedisBridgeCore core;

    @Override
    public void onStartup() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveResource("config.yml");
        proxyId = getConfig().getString("id");

        setupCore();
        setupHandlers();
        setupCommands();
        setupEvents();

        getProxy().getScheduler().scheduleRepeating(new InstanceRegistrationTask(), 1);

        getLogger().info("{}RedisBridge Plugin Enabled", Color.GREEN);
    }

    private void setupCore() {
        Configuration config = getConfig();

        core = new RedisBridgeCore();
        core.connect(
                config.getString("redis.host"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                new Logger()
        );
    }

    private void setupHandlers() {
        getProxy().setJoinHandler(new LobbyJoinHandler());
        getProxy().setReconnectHandler(new ReconnectLobbyHandler());
    }

    private void setupCommands() {
        CommandMap map = getProxy().getCommandMap();
        map.unregisterCommand("wdlist");
        map.registerCommand(new WhereAmICommand());
        //map.registerCommand(new ListCommand());
    }

    private void setupEvents() {
        EventManager manager = getProxy().getEventManager();
        manager.subscribe(ProxyPingEvent.class, ProxyEvents::onPing);
        manager.subscribe(ProxyQueryEvent.class, ProxyEvents::onQuery);
        manager.subscribe(ServerTransferRequestEvent.class, ProxyEvents::onTransferRequest);
    }

    @Override
    public void onDisable() {
        if (core != null) {
            core.close();
        }

        getLogger().info("{}RedisBridge Plugin Disabled", Color.RED);
    }
}
