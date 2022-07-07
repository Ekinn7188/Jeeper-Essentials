package jeeper.essentials.listeners;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
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
        CraftPlayer craftPlayer = (CraftPlayer) e.getPlayer();
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        if (serverPlayer.isRealPlayer) {
            lastLocation.put(e.getPlayer().getUniqueId(), e.getFrom());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        lastLocation.put(e.getEntity().getUniqueId(), e.getEntity().getLocation());
    }
}
