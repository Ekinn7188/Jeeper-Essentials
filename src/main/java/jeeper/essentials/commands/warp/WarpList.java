package jeeper.essentials.commands.warp;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class WarpList extends PluginCommand {
    private static final Config config = Main.getPlugin().config();
    static DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "warps";
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        getWarps(sender);
    }

    protected static void getWarps(CommandSender sender) {
        var warps = dslContext.select(Tables.WARPS.WARPNAME).from(Tables.WARPS).fetch().getValues(Tables.WARPS.WARPNAME);

        if (warps.size() > 0){
            StringBuilder commaSeperatedWarps = new StringBuilder(String.join(", ", warps));

            if (sender instanceof Player player) {
                commaSeperatedWarps = new StringBuilder();
                for (int i = 0; warps.size() > i; i++) {
                    if (warps.get(i).equalsIgnoreCase("spawn")) {
                        continue;
                    }

                    var permission = DatabaseTools.firstString(dslContext.select(Tables.WARPS.WARPPERMISSION).from(Tables.WARPS)
                            .where(Tables.WARPS.WARPNAME.equalIgnoreCase(warps.get(i))).fetchAny());

                    if (permission == null || player.hasPermission(permission)) {
                        commaSeperatedWarps.append(warps.get(i));
                        if (i != warps.size() - 1) {
                            if (warps.get(i++).equals("spawn")) {
                                continue;
                            }
                            commaSeperatedWarps.append(", ");
                        }
                    }
                }
            }
            sender.sendMessage(MessageTools.parseFromPath(config, "Warp List", Placeholder.parsed("warps", commaSeperatedWarps.toString())));
        } else {
            sender.sendMessage(MessageTools.parseFromPath(config, "No Warps"));
        }
    }

}
