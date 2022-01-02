package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class AFK extends PluginCommand {
    public static HashMap<UUID, Boolean> isAFK = new HashMap<>();
    ConfigSetup config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "afk";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.AFK;
    }

    @Override
    public void execute(Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        if (isAFK.containsKey(uuid)){
            if (isAFK.get(uuid)) {
                Bukkit.broadcast(MessageTools.parseFromPath(config, "Player Not AFK", Template.template("player", player.displayName())));
                italicizeName(player);
                isAFK.put(player.getUniqueId(), false);
                TabMenu.updateTab();
                return;
            }
            Bukkit.broadcast(MessageTools.parseFromPath(config, "Player AFK", Template.template("player", player.displayName())));
            italicizeName(player);
            isAFK.put(player.getUniqueId(), true);
            TabMenu.updateTab();
            return;
        }
        Bukkit.broadcast(MessageTools.parseFromPath(config, "Player AFK", Template.template("player", player.displayName())));
        italicizeName(player);
        isAFK.put(player.getUniqueId(), true);
        TabMenu.updateTab();
    }

    private void italicizeName(Player player) {
        String nickname = Main.getPlugin().getDslContext().select(Tables.USERS.USERNICKNAME).from(Tables.USERS)
                .where(Tables.USERS.USERID.eq(DatabaseTools.getUserID(player.getUniqueId())))
                .fetch().getValue(0, Tables.USERS.USERNICKNAME);

        if (nickname == null) {
            player.displayName(MessageTools.parseText("<italic>" + player.getName()));
            return;
        }

        player.displayName(MessageTools.parseText("<italic>" + nickname));

    }
}
