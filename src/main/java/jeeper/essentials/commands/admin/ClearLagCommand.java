package jeeper.essentials.commands.admin;

import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.lag.ClearLag;
import org.bukkit.command.CommandSender;

public class ClearLagCommand extends PluginCommand {
    @Override
    public String getName() {
        return "clearlag";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.CLEARLAG;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ClearLag.clearLag();
    }
}
