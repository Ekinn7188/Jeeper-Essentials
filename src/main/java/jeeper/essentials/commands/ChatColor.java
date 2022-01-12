package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class ChatColor extends PluginCommand {
    DSLContext dslContext = Main.getPlugin().getDslContext();
    private static final ConfigSetup config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "chatcolor";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.CHATCOLOR;
    }

    @Override
    public void execute(Player player, String[] args) {

        if (args.length > 0) {
            if (!MessageTools.parseText(args[0]).equals(Component.text(args[0]))) {
                int userID = DatabaseTools.getUserID(player.getUniqueId());

                if (userID == -1) {
                    DatabaseTools.addUser(player.getUniqueId());
                    userID = DatabaseTools.getUserID(player.getUniqueId());
                }

                dslContext.update(Tables.USERS).set(Tables.USERS.CHATCOLOR, args[0])
                        .where(Tables.USERS.USERID.eq(userID)).execute();
                player.sendMessage(MessageTools.parseFromPath(config, "Chat Color Set"));

            } else {
                player.sendMessage(MessageTools.parseFromPath(config, "Invalid Chat Color"));
            }
            return;
        }

        player.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Template.template("command", "/chatcolor {color}")));

    }
}
