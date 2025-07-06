package net.josscoder.redisbridge.waterdogpe.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import net.josscoder.redisbridge.waterdogpe.RedisBridgePlugin;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami", CommandSettings.builder()
                .setDescription("Get the instance you are currently in")
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            return true;
        }

        ServerInfo serverInfo = player.getServerInfo();
        if (serverInfo == null) {
            return true;
        }

        player.sendMessage("§bYou are on proxy §f%s §band server §f%s.".formatted(
                RedisBridgePlugin.getInstance().getProxyId(),
                serverInfo.getServerName()
        ));


        return true;
    }
}