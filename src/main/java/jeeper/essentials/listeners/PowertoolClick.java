package jeeper.essentials.listeners;

import jeeper.essentials.Main;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PowertoolClick implements Listener {

    @EventHandler
    public void onPowertoolUse(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }

        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "powertool");

        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(key, PersistentDataType.STRING)) {
            String command = container.get(key, PersistentDataType.STRING);
            if (command == null) {
                return;
            }
            e.getPlayer().chat(command);
        }


    }
}
