package jeeper.essentials.commands.warp;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class DeleteHome extends PluginCommand {

    private static final ConfigSetup config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "delhome";
    }

    @Override
    public void execute(Player player, String[] args) {

        int userID = DatabaseTools.getUserID(player.getUniqueId());
        String homeName;

        if (args.length == 0){
            homeName = "home";
        } else {
            homeName = args[0];
        }

        Location loc = player.getLocation();

        var home = DatabaseTools.firstString(dslContext.select(Tables.HOMES.HOMENAME).from(Tables.HOMES)
                .where(Tables.HOMES.USERID.eq(userID).and(Tables.HOMES.HOMENAME.equalIgnoreCase(homeName))).fetchAny());

        if (home == null){
            player.sendMessage(MessageTools.parseFromPath(config, "Home Doesnt Exist", Template.template("name", homeName)));
            return;
        }
        dslContext.delete(Tables.HOMES).where(Tables.HOMES.USERID.eq(userID).and(Tables.HOMES.HOMENAME.equalIgnoreCase(home))).execute();
        player.sendMessage(MessageTools.parseFromPath(config,"Home Deleted", Template.template("name", home)));
    }
}
