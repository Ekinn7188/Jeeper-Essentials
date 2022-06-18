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

public class Mute extends PluginCommand {
    Config config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.MUTE;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/mute {player}")));
            return;
        }

        if (sender instanceof Player p) {
            try{
                Inventory inventory = Punishments.getPunishmentsMenu(Punishment.MUTE, p, args[0]);
                p.openInventory(Objects.requireNonNull(inventory));
                // menu is added to map if it's not null
                Punishments.openMenus.add(p.getUniqueId());
                return;
            } catch (NullPointerException e) {
                //if there's an exception, then try to run the console-friendly command
            }
        }

        //mute {player} 0d0h0m {reason}
        if (args.length >= 2) {
            Punishments.consolePunishment(Punishment.MUTE, sender, args);
        } else {
            sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage", Placeholder.parsed("command", "/mute {player} {time | 0d0h0m} {reason}")));
        }
    }
}
