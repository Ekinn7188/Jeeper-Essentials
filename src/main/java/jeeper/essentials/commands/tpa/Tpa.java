package jeeper.essentials.commands.tpa;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tpa extends PluginCommand {

    /**
     * The map of pending tpa requests.<br>
     * <b>Key:</b> UUID of the requester<br>
     * <b>Value:</b> UUID of the target
     */
    protected static Map<UUID, UUID> tpaRequests = new HashMap<>();

    @Override
    public String getName() {
        return "tpa";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.TPA;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage", Placeholder.parsed("command", "/tpa {player}")));
            return;
        }

        OfflinePlayer recipient = UUIDTools.checkNameAndUUID(player, args[0]);

        if (recipient == null) {
            return;
        }

        if (!recipient.isOnline()) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline", Placeholder.parsed("player", recipient.getName() == null ? args[0] : recipient.getName())));
            return;
        }

        Player onlineRecipient = (Player) recipient;

        tpaRequests.put(player.getUniqueId(), recipient.getUniqueId());

        player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Tpa Request Sent",
                Placeholder.parsed("player", recipient.getName() == null ? args[0] : recipient.getName())));

        // Make Accept / Decline Buttons work

        TextComponent acceptButton = Component.text("Accept").clickEvent(ClickEvent.runCommand("/tpaccept " + player.getName()));
        TextComponent denyButton = Component.text("Deny").clickEvent(ClickEvent.runCommand("/tpdeny " + player.getName()));

        onlineRecipient.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Tpa Request Received",
                Placeholder.parsed("player", player.getName()), Placeholder.component("accept", acceptButton),
                Placeholder.component("deny", denyButton)));

        Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> tpaRequests.remove(player.getUniqueId()), 20 * 60); // 1 minute wait

    }
}
