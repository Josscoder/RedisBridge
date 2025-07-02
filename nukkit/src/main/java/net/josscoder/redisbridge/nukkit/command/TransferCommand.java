package net.josscoder.redisbridge.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer", "Transfer to a server", "/transfer <serverID>");
        //TODO: add permission
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return false;
    }
}