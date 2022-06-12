package jeeper.essentials.commands.warp;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.Countdown;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class Home extends PluginCommand {
    private static final Config config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "home";
    }

    @Override
    public void execute(Player player, String[] args) {
        int userID = DatabaseTools.getUserID(player.getUniqueId());

        if (args.length > 0){

            var locationString = DatabaseTools.firstString(dslContext.select(Tables.HOMES.HOMELOCATION).from(Tables.HOMES)
                    .where(Tables.HOMES.USERID.eq(userID).and(Tables.HOMES.HOMENAME.eq(args[0]))).fetchAny());

            if (locationString == null) { //if null, home doesn't exist
                player.sendMessage(MessageTools.parseFromPath(config, "Home Doesnt Exist", Placeholder.parsed("name", args[0])));
                return;
            }

            Countdown.startCountdown(player, locationString, args[0], Main.getPlugin(), 3);

        } else {
            var locationString = DatabaseTools.firstString(dslContext.select(Tables.HOMES.HOMELOCATION).from(Tables.HOMES)
                    .where(Tables.HOMES.USERID.eq(userID).and(Tables.HOMES.HOMENAME.equalIgnoreCase("home"))).fetchAny());

            if (locationString == null){
                player.sendMessage(MessageTools.parseFromPath(config,"Home Doesnt Exist", Placeholder.parsed("name", "home")));
                return;
            }

            Countdown.startCountdown(player, locationString, "home", Main.getPlugin(), 3);
        }
    }

}