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

public class Warp extends PluginCommand {
    private static final Config config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "warp";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length > 0){
            String warpLocation = DatabaseTools.firstString(dslContext.select(Tables.WARPS.WARPLOCATION).from(Tables.WARPS)
                    .where(Tables.WARPS.WARPNAME.equalIgnoreCase(args[0])).fetchAny());

            String warpPermission = DatabaseTools.firstString(dslContext.select(Tables.WARPS.WARPPERMISSION).from(Tables.WARPS)
                    .where(Tables.WARPS.WARPNAME.equalIgnoreCase(args[0])).fetchAny());
            if (warpLocation == null || (warpPermission != null && !player.hasPermission(warpPermission))) {
                player.sendMessage(MessageTools.parseFromPath(config, "Warp Doesnt Exist", Placeholder.parsed("name", args[0])));
                return;
            }
            Countdown.startCountdown(player, warpLocation, args[0], Main.getPlugin());
        } else {
            WarpList.getWarps(player);
        }
    }


}
