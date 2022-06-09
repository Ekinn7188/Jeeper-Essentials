package jeeper.essentials.commands.warp;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jooq.DSLContext;

public class DeleteWarp extends PluginCommand {

    private static final Config config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "delwarp";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.DELETEWARP;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0){

            var warpID = DatabaseTools.firstInt(dslContext.select(Tables.WARPS.WARPID).from(Tables.WARPS)
                    .where(Tables.WARPS.WARPNAME.equalIgnoreCase(args[0])).fetchAny());

            if (warpID == -1){
                sender.sendMessage(MessageTools.parseFromPath(config, "Warp Doesnt Exist", Placeholder.parsed("name", args[0])));
                return;
            }

            dslContext.delete(Tables.WARPS).where(Tables.WARPS.WARPID.eq(warpID)).execute();
            sender.sendMessage(MessageTools.parseFromPath(config,"Warp Deleted", Placeholder.parsed("name", args[0])));
        }
    }

}
