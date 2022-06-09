package jeeper.essentials.commands.warp;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.NumberAfterPermission;
import jeeper.utils.LocationParser;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class SetHome extends PluginCommand {

    private static final Config config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "sethome";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.SETHOME;
    }

    @Override
    public void execute(Player player, String[] args) {
        Location loc = player.getLocation();
        int largestSetHomeSize = NumberAfterPermission.get(player, "jeeper.sethome.");

        if (largestSetHomeSize == -1) {
            largestSetHomeSize = 0;
        }

        int userID = DatabaseTools.getUserID(player.getUniqueId());

        var homesForUser = dslContext.select(Tables.HOMES.HOMENAME).from(Tables.HOMES).where(Tables.HOMES.USERID.eq(userID));

        String homeName = "home";

        if (args.length != 0) {
            homeName = args[0];
        }

        for (var record : homesForUser) {
            if (record.value1().equals(args[0])) {
                dslContext.update(Tables.HOMES).set(Tables.HOMES.HOMELOCATION, LocationParser.roundedLocationToString(loc))
                        .where(Tables.HOMES.USERID.eq(userID).and(Tables.HOMES.HOMENAME.eq(record.value1()))).execute();
                player.sendMessage(MessageTools.parseFromPath(config, "Home Created", Placeholder.parsed("name", homeName)));
                return;
            }
        }

        if (homesForUser.execute() < largestSetHomeSize) {
            dslContext.insertInto(Tables.HOMES).columns(Tables.HOMES.USERID, Tables.HOMES.HOMENAME, Tables.HOMES.HOMELOCATION)
                    .values(userID, homeName, LocationParser.roundedLocationToString(loc)).execute();
            player.sendMessage(MessageTools.parseFromPath(config, "Home Created", Placeholder.parsed("name", homeName)));
            return;
        }
        player.sendMessage(MessageTools.parseFromPath(config, "Too Many Homes", Placeholder.parsed("number", String.valueOf(largestSetHomeSize))));
    }
}
