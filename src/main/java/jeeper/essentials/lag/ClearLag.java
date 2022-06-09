package jeeper.essentials.lag;

import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class ClearLag {

    private static final Config config = Main.getPlugin().config();

    public static void clearLag() {

        //kill entities
        List<List<Entity>> entities = new ArrayList<>();
        Bukkit.getServer().getWorlds().forEach(world -> entities.add(world.getEntities()));

        for (int i = 0; i < entities.size(); i++) {
            List<Entity> entityList = entities.get(i);
            for (int j = 0; j < entityList.size(); j++) {
                Entity entity = entityList.get(j);
                if ((entity.getType() == EntityType.BOAT || entity.getType() == EntityType.MINECART) && entity.getPassengers().size() > 0) {
                    entityList.remove(entity);
                    entity.getPassengers().forEach(entityList::remove);
                }
            }
            //new list without vehicles and passengers
            entities.set(i, entityList);
        }

        //kill all items, arrows, falling blocks, exp orbs, minecarts, and boats left in list
        entities.forEach(e -> e.forEach(entity -> {
            if (entity.getType().equals(EntityType.DROPPED_ITEM) || entity.getType().equals(EntityType.ARROW)
                    || entity.getType().equals(EntityType.FALLING_BLOCK) || entity.getType().equals(EntityType.EXPERIENCE_ORB)
                    || entity.getType().equals(EntityType.MINECART) || entity.getType().equals(EntityType.BOAT)
                    || entity.getType().equals(EntityType.PRIMED_TNT)) {
                entity.remove();
            }
        }));

        Bukkit.broadcast(MessageTools.parseFromPath(config, "Lag Cleared"));
    }

    public static void clearLagLoop() {
        //19 minutes, then 1 minute warning will run inside the task, making it a total of 20 minutes
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), () -> {
            //1 minute message
            Bukkit.broadcast(MessageTools.parseFromPath(config, "Lag Time", Placeholder.parsed("time", "1 minute")));

            //30 second message, wait 30 seconds
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                Bukkit.broadcast(MessageTools.parseFromPath(config, "Lag Time", Placeholder.parsed("time", "30 seconds")));

                //10 second message, wait 20 seconds
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> {
                    Bukkit.broadcast(MessageTools.parseFromPath(config, "Lag Time", Placeholder.parsed("time", "10 seconds")));
                    //now clear
                    clearLag();
                }, 400L);
            }, 600L);
        }, 22800L, 22800L);
    }
}
