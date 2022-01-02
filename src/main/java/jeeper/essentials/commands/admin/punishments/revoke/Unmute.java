package jeeper.essentials.commands.admin.punishments.revoke;

import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.utils.MessageTools;
import org.bukkit.command.CommandSender;

public class Unmute extends PluginCommand {

    @Override
    public String getName() {
        return "unmute";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.MUTE;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseText("&cUsage: /unmute {player}"));
            return;
        }
        RevokePunishment.revoke(Punishment.MUTE, sender, args[0]);
    }
}