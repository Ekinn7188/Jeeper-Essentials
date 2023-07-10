package jeeper.essentials.commands.tpa;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TpDeny extends PluginCommand {
    @Override
    public String getName() {
        return "tpdeny";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.TPA;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage", Placeholder.parsed("command", "/tpdeny {player}")));
            return;
        }

        OfflinePlayer target = UUIDTools.getOfflinePlayer(player, args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline"));
            return;
        }

        if (!Tpa.tpaRequests.containsKey(target.getUniqueId())) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "No Tpa Request",
                    Placeholder.parsed("player", target.getName() == null ? args[0] : target.getName())));
            return;
        }

        Tpa.tpaRequests.remove(target.getUniqueId());

        ((Player) target).sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Tpa Request Denied",
                Placeholder.parsed("player", player.getName())));

        player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Tpa Successfully Denied",
                Placeholder.parsed("player", target.getName() == null ? args[0] : target.getName())));



    }
}
