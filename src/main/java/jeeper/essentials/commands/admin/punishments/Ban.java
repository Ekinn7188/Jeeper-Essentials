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

import java.util.Objects;

public class Ban extends PluginCommand {
    Config config = Main.getPlugin().config();


    @Override
    public String getName() {
        return "ban";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.BAN;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/ban {player}")));
            return;
        }

        if (sender instanceof Player p && args.length == 1) {
            try{
                p.openInventory(Objects.requireNonNull(Punishments.getPunishmentsMenu(Punishment.BAN, p, args[0])));
                return;
            } catch (NullPointerException e) {
                //try console command below
            }
        }

        //ban {player} 0d0h0m {reason}
        if (args.length >= 2) {
            Punishments.consolePunishment(Punishment.BAN, sender, args);
        } else {
            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/ban {player} {time | 0d0h0m} {reason}")));
        }




    }


}
