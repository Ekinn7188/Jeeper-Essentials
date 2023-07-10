package jeeper.essentials.commands.admin.gamemode;

import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

public class gmc extends PluginCommand {
    
    @Override
    public String getName() {
        return "gmc";
    }
    
    @Override
    protected Permission getPermissionType() {
        return Permission.GAMEMODE;
    }
    
    @Override
    public boolean isRequiresPlayer() {
        return false;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        GameModeChanger.command(sender, args, GameMode.CREATIVE);
    }
}
