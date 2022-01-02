package jeeper.essentials.tools;

import jeeper.essentials.Main;
import jeeper.utils.LocationParser;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class Countdown {
    private static ConfigSetup config = Main.getPlugin().config();

    private static final HashMap<UUID, Integer> tasks = new HashMap<>();

    public static HashMap<UUID, Integer> getTasks() {
        return tasks;
    }

    public static void startCountdown(Player player, String coordsLocation, String destination, Main plugin){

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            player.teleport(LocationParser.stringToLocation(coordsLocation));
            player.sendMessage(MessageTools.parseFromPath(config, "Teleport Success", Template.template("location", destination)));
            return;
        }

        if (tasks.containsKey(player.getUniqueId())){
            Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
        }

        tasks.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int time = 5;
            @Override
            public void run() {
                if(time == 5){
                    player.sendMessage(MessageTools.parseFromPath(config, "Dont Move Message"));
                }
                if (time == 0){
                    player.teleport(LocationParser.stringToLocation(coordsLocation));
                    player.sendMessage(MessageTools.parseFromPath(config, "Teleport Success", Template.template("location", destination)));
                    Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
                    tasks.remove(player.getUniqueId());
                }
                else{
                    final Title.Times times = Title.Times.of(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(500));
                    final Title title = Title.title(MessageTools.parseFromPath(config, "Teleport Countdown", Template.template("time", String.valueOf(time)),
                            Template.template("location", destination)), Component.empty(), times);
                    player.showTitle(title);
                    time--;
                }
            }
        }, 0, 20));
    }

}
