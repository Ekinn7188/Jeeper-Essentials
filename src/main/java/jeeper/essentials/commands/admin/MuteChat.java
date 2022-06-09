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

public class MuteChat extends PluginCommand {

    public static boolean chatMuted = false;
    private static final Config config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "mutechat";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.MUTECHAT;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        chatMuted = !chatMuted;
        Component commandSender = (sender instanceof Player) ? ((Player) sender).displayName() : Component.text("console");

        if (chatMuted){
            Bukkit.broadcast(MessageTools.parseFromPath(config,"Chat Muted By Message", Placeholder.component("player", commandSender)));
        } else {
            Bukkit.broadcast(MessageTools.parseFromPath(config, "Chat Unmuted By Message", Placeholder.component("player", commandSender)));
        }
    }
}
