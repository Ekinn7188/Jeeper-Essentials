package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.listeners.TeleportBack;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Back extends PluginCommand {
    public static final ConfigSetup config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "back";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.BACK;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (TeleportBack.lastLocation.containsKey(player.getUniqueId())) {
            player.teleport(TeleportBack.lastLocation.get(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.COMMAND);
            player.sendMessage(MessageTools.parseFromPath(config, "Back Message"));
            TeleportBack.lastLocation.remove(player.getUniqueId());

        } else {
            player.sendMessage(MessageTools.parseFromPath(config, "Cant Back"));
        }
    }
}
