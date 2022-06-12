package jeeper.essentials.tools;

import jeeper.essentials.Main;
import jeeper.utils.LocationParser;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

public class Countdown {
    private static final Config config = Main.getPlugin().config();

    private static final HashMap<UUID, Integer> tasks = new HashMap<>();

    public static HashMap<UUID, Integer> getTasks() {
        return tasks;
    }

    public static void startCountdown(Player player, Location destination, String destinationName, Main plugin, int duration) {
        String locationString = LocationParser.locationToString(destination);
        startCountdown(player, locationString, destinationName, plugin, duration);
    }

    public static void startCountdown(Player player, String destinationString, String destinationName, Main plugin, int duration){

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            player.teleport(LocationParser.stringToLocation(destinationString));
            player.sendMessage(MessageTools.parseFromPath(config, "Teleport Success", Placeholder.parsed("location", destinationName)));
            return;
        }

        if (tasks.containsKey(player.getUniqueId())){
            Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
        }

        tasks.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int time = duration;
            @Override
            public void run() {
                if(time == 5){
                    player.sendMessage(MessageTools.parseFromPath(config, "Dont Move Message"));
                }
                if (time == 0){
                    player.teleport(LocationParser.stringToLocation(destinationString));
                    player.sendMessage(MessageTools.parseFromPath(config, "Teleport Success", Placeholder.parsed("location", destinationName)));
                    Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
                    tasks.remove(player.getUniqueId());
                }
                else{
                    final Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(500), Duration.ofMillis(500));
                    final Title title = Title.title(MessageTools.parseFromPath(config, "Teleport Countdown", Placeholder.parsed("time", String.valueOf(time)),
                            Placeholder.parsed("location", destinationName)), Component.empty(), times);
                    player.showTitle(title);
                    time--;
                }
            }
        }, 0, 20));
    }

}
