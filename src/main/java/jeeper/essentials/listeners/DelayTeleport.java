package jeeper.essentials.listeners;

import jeeper.essentials.Main;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DelayTeleport implements Listener {

    //used to prevent "{player} moved too quickly!" messages on teleport
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            return;
        }
        CraftPlayer craftPlayer = (CraftPlayer) e.getPlayer();
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        if (serverPlayer.isRealPlayer) {
            e.setCancelled(true);
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), () -> e.getPlayer().teleport(e.getTo(), PlayerTeleportEvent.TeleportCause.UNKNOWN), 5L);
        }
    }
}
