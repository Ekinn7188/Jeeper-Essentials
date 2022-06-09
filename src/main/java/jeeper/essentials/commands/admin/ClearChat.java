package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChat extends PluginCommand {
    private static final Config config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "clearchat";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.CLEARCHAT;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for(int i = 0; i < 300; i++){
            Bukkit.broadcast(Component.text(""));
        }
        if (sender instanceof Player){
            Bukkit.broadcast(MessageTools.parseFromPath(config, "Chat Cleared By Message", Placeholder.component("player", ((Player) sender).displayName())));
        } else {
            Bukkit.broadcast(MessageTools.parseFromPath(config, "Chat Cleared By Message", Placeholder.parsed("player", "console")));
        }
    }
}
