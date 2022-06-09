package jeeper.essentials.listeners;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;

public class OverrideBukkit implements Listener {

    //for console
    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        if (processNewCommands(e.getSender(), e.getCommand())) {
            e.setCancelled(true);
        }
    }

    //for players
    @EventHandler
    public void onPlayerCommandSend(PlayerCommandPreprocessEvent e) {
        if (processNewCommands(e.getPlayer(), e.getMessage())){
            e.setCancelled(true);
        }
    }

    private boolean processNewCommands(CommandSender sender, String command) {
        if (command.equalsIgnoreCase("/tps")) {
            if (!sender.hasPermission(Permission.TPS.getName())) {
                sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "No Command Permission"));
                return true;
            }
            String[] tps = Arrays.stream(Bukkit.getServer().getTPS()).map(x -> ((int)(x*100))/100.0).mapToObj(String::valueOf).toArray(String[]::new);
            sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Tps Message",
                    Placeholder.parsed("1m", tps[0]), Placeholder.parsed("5m", tps[1]), Placeholder.parsed("15m", tps[2])));
            return true;
        } else if (command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins") || command.equalsIgnoreCase("/bukkit:plugins") || command.equalsIgnoreCase("/bukkit:pl")) {
            if (sender.hasPermission(Permission.PLUGIN.getName())) {
                return false;
            } else {
                sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "No Command Permission"));
                return true;
            }
        }
        return false;
    }

}
