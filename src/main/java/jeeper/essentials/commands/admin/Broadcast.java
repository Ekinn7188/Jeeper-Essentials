package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class Broadcast extends PluginCommand {
    private static final Config config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "broadcast";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.BROADCAST;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            String message = String.join(" ", args);
            Component messageComponent = MessageTools.parseText(message);

            for (String url : MessageTools.fetchURLs(message)) {
                messageComponent = messageComponent.clickEvent(ClickEvent.openUrl(url));
            }

            Bukkit.broadcast(MessageTools.parseFromPath(config, "Broadcast Prefix").append(messageComponent));
        }
    }
}