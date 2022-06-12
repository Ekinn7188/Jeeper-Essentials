package jeeper.essentials.commands.tpa;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.tools.Countdown;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TpAccept extends PluginCommand {
    @Override
    public String getName() {
        return "tpaccept";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.TPA;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage", Placeholder.parsed("command", "/tpaccept {player}")));
            return;
        }

        OfflinePlayer offlineTarget = UUIDTools.checkNameAndUUID(player, args[0]);

        if (offlineTarget == null) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline"));
            return;
        }

        if (!offlineTarget.isOnline()) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline", Placeholder.parsed("player", offlineTarget.getName() == null ? args[0] : offlineTarget.getName())));
            return;
        }

        Player target = (Player) offlineTarget;

        if (!Tpa.tpaRequests.containsKey(target.getUniqueId())) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "No Tpa Request",
                    Placeholder.parsed("player", target.getName())));
            return;
        }

        Countdown.startCountdown(player, target.getLocation(), target.getName(), Main.getPlugin(), 5);
        Tpa.tpaRequests.remove(target.getUniqueId());
    }
}
