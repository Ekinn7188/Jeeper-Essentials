package jeeper.essentials.commands.admin.gamemode;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeChanger {
    
    public static void command(CommandSender sender, String[] args, GameMode gameMode) {
        if (args.length == 0) {
            if (sender instanceof Player p) {
                p.setGameMode(gameMode);
                sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Updated Gamemode for Self",
                        Placeholder.component("gamemode", Component.text(gameMode.name().toLowerCase()))));
                return;
            }
            else {
                sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Only Command"));
                return;
            }
        }

        
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Player Is Offline", Placeholder.component("player", Component.text(args[0]))));
            return;
        }
        
        player.setGameMode(gameMode);
        sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Updated Gamemode",
                            Placeholder.component("player", Component.text(args[0])),
                            Placeholder.component("gamemode", Component.text(gameMode.name().toLowerCase()))));
        
    }
}
