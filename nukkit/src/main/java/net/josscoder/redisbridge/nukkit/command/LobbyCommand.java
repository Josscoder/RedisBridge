package net.josscoder.redisbridge.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import net.josscoder.redisbridge.core.instance.InstanceInfo;
import net.josscoder.redisbridge.core.instance.InstanceManager;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "Return to lobby", "/lobby", new String[]{"hub"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Must be a player!");

            return false;
        }

        Player player = (Player) sender;

        InstanceInfo availableInstance = InstanceManager.getInstance().selectAvailableInstance(
                "lobby",
                InstanceManager.SelectionStrategy.MOST_PLAYERS_AVAILABLE
        );
        if (availableInstance == null) {
            player.sendMessage(TextFormat.RED + "There are no lobbies available!");

            return false;
        }

        player.transfer(availableInstance.getId(), 0);

        return true;
    }
}
