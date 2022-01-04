package jeeper.essentials.listeners;

import jeeper.essentials.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DelayTeleport implements Listener {

    //used to prevent "{player} moved too quickly!" messages on teleport
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            return;
        }
        event.setCancelled(true);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> event.getPlayer().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.UNKNOWN), 5L);
    }
}
