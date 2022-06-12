package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class TpHere extends PluginCommand {
    @Override
    public String getName() {
        return "tphere";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.VANILLA_TELEPORT;
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length == 0) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage", Placeholder.parsed("command", "/tphere {player}")));
            return;
        }

        Player target = player.getServer().getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline"));
            return;
        }

        target.teleport(player.getLocation());
        player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Tphere Success",
                Placeholder.parsed("player", target.getName())));

        target.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Teleported to Player", Placeholder.parsed("player", player.getName())));
    }
}
