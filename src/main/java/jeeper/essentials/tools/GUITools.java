package jeeper.essentials.tools;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class GUITools {

    public static void addBorder(Inventory inventory, Material borderMaterial) {
        int size = inventory.getSize();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, ItemTools.createGuiItem(borderMaterial, Component.text(""), 1));
            inventory.setItem(i+45, ItemTools.createGuiItem(borderMaterial, Component.text(""), 1));
            if (i*9 <= size-9) {
                inventory.setItem(i*9, ItemTools.createGuiItem(borderMaterial, Component.text(""), 1));
            }
            if (i!=0 && i*9-1 <= size-1) {
                inventory.setItem(i*9-1, ItemTools.createGuiItem(borderMaterial, Component.text(""), 1));
            }
        }
    }

}
