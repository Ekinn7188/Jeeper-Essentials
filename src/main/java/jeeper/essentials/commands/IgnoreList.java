package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IgnoreList extends PluginCommand {
    private static final Config config = Main.getPlugin().config();
    private static final DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "ignorelist";
    }

    @Override
    public void execute(Player player, String[] args) {
        int userID = DatabaseTools.getUserID(player.getUniqueId());

        if (userID == -1) {
            DatabaseTools.addUser(player.getUniqueId());
            userID = DatabaseTools.getUserID(player.getUniqueId());
        }

        var ignoredPlayerIDs = dslContext.select(Tables.IGNOREDPLAYERS.IGNOREDPLAYERID)
                .from(Tables.IGNOREDPLAYERS).where(Tables.IGNOREDPLAYERS.USERID.eq(userID)).fetchInto(Integer.class);

        List<String> names = new ArrayList<>();

        for (int ignoredPlayerID : ignoredPlayerIDs) {

            String uuid = DatabaseTools.getUserUUID(ignoredPlayerID);
            if (uuid == null) {
                continue;
            }

            Player ignoredPlayer = Bukkit.getServer().getPlayer(UUID.fromString(uuid));

            if (ignoredPlayer == null) {
                continue;
            }

            names.add(ignoredPlayer.getName());

        }

        if (names.size() > 0) {
            player.sendMessage(MessageTools.parseFromPath(config, "Ignore List", Placeholder.parsed("ignored", String.join(", ", names))));
        } else {
            player.sendMessage(MessageTools.parseFromPath(config, "Ignoring Nobody"));
        }



    }
}
