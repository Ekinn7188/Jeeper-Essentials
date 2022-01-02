package jeeper.essentials.commands.admin.punishments.revoke;

import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.utils.MessageTools;
import org.bukkit.command.CommandSender;

public class Unban extends PluginCommand {

    @Override
    public String getName() {
        return "unban";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.BAN;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseText("&cUsage: /unban {player}"));
            return;
        }
        RevokePunishment.revoke(Punishment.BAN, sender, args[0]);
    }
}
