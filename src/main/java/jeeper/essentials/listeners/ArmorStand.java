package jeeper.essentials.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ArmorStand implements Listener {


    @EventHandler
    public void onEntityMove(EntityMoveEvent e) {
        if (!e.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT)) return;
        if (e.getEntity().getType() == org.bukkit.entity.EntityType.ARMOR_STAND) {
            if (!e.getTo().getChunk().equals(e.getFrom().getChunk())) {
                int standCount = (int) Arrays.stream(e.getEntity().getLocation().getChunk().getEntities()).filter(entity -> entity.getType() == org.bukkit.entity.EntityType.ARMOR_STAND).count();
                if (standCount >= 6) {
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemStack(Material.ARMOR_STAND));
                    e.getEntity().remove();
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!e.getMaterial().equals(Material.ARMOR_STAND)) return;
        Chunk chunk = e.getClickedBlock().getLocation().getChunk();
        var armorStands = Arrays.stream(chunk.getEntities()).filter(entity -> entity.getType() == org.bukkit.entity.EntityType.ARMOR_STAND).toList();
        int standCount = armorStands.size();
        if (standCount >= 6) {
            e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.ARMOR_STAND));
            e.setCancelled(true);
        }
    }
}
