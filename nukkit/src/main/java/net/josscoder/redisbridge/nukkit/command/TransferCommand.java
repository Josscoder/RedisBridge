package net.josscoder.redisbridge.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.instance.InstanceManager;
import net.josscoder.redisbridge.nukkit.RedisBridgePlugin;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer", "Transfer to a server", "/transfer <serverID>");
        setPermission("redisbridge.transfer.command.permission");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Must be a player!");

            return false;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(TextFormat.RED + getUsage());

            return false;
        }

        String serverID = args[0];

        InstanceInfo instance = InstanceManager.getInstance().getInstanceById(serverID);
        if (instance == null) {
            player.sendMessage(TextFormat.RED + "Server does not exist!");

            return false;
        }

        if (instance.getId().equalsIgnoreCase(RedisBridgePlugin.getInstance().getInstanceInfo().getId())) {
            player.sendMessage(TextFormat.RED + "You are already on this server!");

            return false;
        }

        player.transfer(instance.getId(), 0);

        return true;
    }
}