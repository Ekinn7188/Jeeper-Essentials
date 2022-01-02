package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sudo extends PluginCommand {
    @Override
    public String getName() {
        return "sudo";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.SUDO;
    }

    @Override
    public boolean isRequiresPlayer() {
       return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            sender.sendMessage(MessageTools.parseText("&cUsage: /sudo {player} {command}"));
            return;
        }
        Player player = Main.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline",
                    Template.template("player", args[0])));
            return;
        }

        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);

        player.chat(String.join(" ", newArgs));
    }
}
