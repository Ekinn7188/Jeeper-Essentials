package jeeper.essentials.listeners;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.AFK;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class AfkMove implements Listener {

    Config config = Main.getPlugin().config();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (AFK.isAFK.containsKey(e.getPlayer().getUniqueId()) && AFK.isAFK.get(player.getUniqueId())) {
            String nickname = Main.getPlugin().getDslContext().select(Tables.USERS.USERNICKNAME)
                    .from(Tables.USERS).where(Tables.USERS.USERID.eq(DatabaseTools.getUserID(player.getUniqueId())))
                    .fetch().getValue(0, Tables.USERS.USERNICKNAME);
            player.displayName(MessageTools.parseText(Objects.requireNonNullElseGet(nickname, player::getName)));

            Bukkit.broadcast(MessageTools.parseFromPath(config, "Player Not AFK", Placeholder.component("player", player.displayName())));
            AFK.isAFK.put(player.getUniqueId(), false);

            TabMenu.updateTab();
        }
    }
}
