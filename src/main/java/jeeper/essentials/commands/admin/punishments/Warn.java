package jeeper.essentials.commands.admin.punishments;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Objects;

public class Warn extends PluginCommand {
    Config config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.WARN;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/warn {player}")));
            return;
        }

        if (sender instanceof Player p) {
            try{
                Inventory menu = Punishments.getPunishmentsMenu(Punishment.WARN, p, args[0]);
                p.openInventory(Objects.requireNonNull(menu));
                // menu is added to map if it's not null
                Punishments.openMenus.add(p.getUniqueId());
                return;
            } catch (NullPointerException e) {
                //go on to console command
            }
        }

        //warn {player} {reason}
        if (args.length >= 2) {
            Punishments.consolePunishment(Punishment.WARN, sender, args);
        } else {

            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/warn {player} {reason}")));
        }

    }
}
