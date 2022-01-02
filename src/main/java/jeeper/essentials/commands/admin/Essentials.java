package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import org.bukkit.command.CommandSender;

public class Essentials extends PluginCommand {
    private static final ConfigSetup config = Main.getPlugin().config();
    @Override
    public String getName() {
        return "essentials";
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.ESSENTIALS;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Main.getPlugin().config().reload();
                TabMenu.updateTab();

                sender.sendMessage(MessageTools.parseFromPath(config, "Essentials Reloaded"));
            }
        }
    }
}
