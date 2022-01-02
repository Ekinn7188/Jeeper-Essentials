package jeeper.essentials.commands.admin.punishments;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.essentials.listeners.punishments.PunishmentTools;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Arrays;

public class IpBan extends PluginCommand {
    @Override
    public String getName() {
        return "ip-ban";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.BAN_IP;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("&cCorrect Usage: /ipban {player} {reason}");
            return;
        }

        Player target = Main.getPlugin().getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline",
                    Template.template("player", args[0])));
            return;
        }

        var address = target.getAddress();
        if (address == null) {
            sender.sendMessage(MessageTools.parseText("&cThere was an issue getting <player>'s IP address."));
            return;
        }

        String ip = target.getAddress().getAddress().getHostAddress();

        String reason = null;

        if (args.length > 1) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        PunishmentTools.addPunishmentToDB(sender, Punishment.IP_BAN, (sender instanceof Player ? ((Player) sender).getUniqueId().toString() : "CONSOLE"), ip, target, LocalDateTime.now(), null, reason);


    }
}
