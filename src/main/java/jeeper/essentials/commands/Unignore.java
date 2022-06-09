package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class Unignore extends PluginCommand {

    private static final Config config = Main.getPlugin().config();
    private static final DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "unignore";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length >= 1) {
            int[] IDs = Ignore.getTargetAndPlayerID(args[0], player);
            if (IDs == null) {
                return;
            }

            int targetID = IDs[0];
            int userID = IDs[1];

            int deletedColumns = dslContext.deleteFrom(Tables.IGNOREDPLAYERS)
                    .where(Tables.IGNOREDPLAYERS.IGNOREDPLAYERID.eq(targetID)
                            .and(Tables.IGNOREDPLAYERS.USERID.eq(userID))).execute();

            if (deletedColumns > 0) {
                player.sendMessage(MessageTools.parseFromPath(config, "Unignored", Placeholder.parsed("player", args[0])));
            } else {
                player.sendMessage(MessageTools.parseFromPath(config, "Player Not Ignored", Placeholder.parsed("player", args[0])));
            }
        } else {
            player.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/unignore <player>")));
        }
    }
}
