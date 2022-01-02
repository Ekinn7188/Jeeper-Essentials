package jeeper.essentials.commands.warp;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.Countdown;
import jeeper.utils.MessageTools;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

public class Spawn extends PluginCommand {
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public void execute(Player player, String[] args) {

        var spawn = DatabaseTools.firstString(dslContext.select(Tables.WARPS.WARPLOCATION).from(Tables.WARPS)
                .where(Tables.WARPS.WARPNAME.equalIgnoreCase("spawn")).fetchAny());

        if (spawn == null){
            player.sendMessage(MessageTools.parseText("<red>Spawn doesn't exist yet! Make sure to set it with <dark_red>/setspawn</dark_red>!</red>"));
            return;
        }
        Countdown.startCountdown(player, spawn, "spawn", Main.getPlugin());
    }

}
