package net.josscoder.redisbridge.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "Return to lobby", "/lobby", new String[]{"hub"});
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return false;
    }
}
