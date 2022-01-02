package jeeper.essentials.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class TeleportBack implements Listener {
    public static HashMap<UUID, Location> lastLocation = new HashMap<>();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        lastLocation.put(e.getPlayer().getUniqueId(), e.getFrom());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        lastLocation.put(e.getEntity().getUniqueId(), e.getEntity().getLocation());
    }
}
