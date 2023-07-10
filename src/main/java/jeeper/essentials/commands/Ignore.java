package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class Ignore extends PluginCommand {
    private static final Config config = Main.getPlugin().config();
    private static final DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "ignore";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length >= 1) {
            int[] IDs = getTargetAndPlayerID(args[0], player);
            if (IDs == null) {
                return;
            }

            int targetID = IDs[0];
            int userID = IDs[1];

            dslContext.insertInto(Tables.IGNOREDPLAYERS, Tables.IGNOREDPLAYERS.USERID, Tables.IGNOREDPLAYERS.IGNOREDPLAYERID)
                    .values(userID, targetID).execute();
            player.sendMessage(MessageTools.parseFromPath(config, "Ignore Success", Placeholder.parsed("player", args[0])));
        } else {
            player.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/ignore {player}")));
        }
    }

    public static int[] getTargetAndPlayerID(String name, Player player) {

        OfflinePlayer target = UUIDTools.getOfflinePlayer(player, name);
        if (target == null) {
            return null;
        }

        int targetID = DatabaseTools.getUserID(target.getUniqueId());
        if (targetID == -1) {
            player.sendMessage(MessageTools.parseFromPath(config, "Player Hasnt Logged In", Placeholder.parsed("player", name)));
        }

        int userID = DatabaseTools.getUserID(player.getUniqueId());
        if (userID == -1) {
            DatabaseTools.addUser(player.getUniqueId());
            userID = DatabaseTools.getUserID(player.getUniqueId());
        }

        return new int[]{targetID, userID};

    }

}
