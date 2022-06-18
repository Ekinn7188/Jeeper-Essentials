package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Enchant extends PluginCommand {
    @Override
    public String getName() {
        return "enchant";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.ENCHANT;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage",
                    Placeholder.parsed("command", "/enchant {enchantment} {level}")));
            return;
        }

        int level;
        try {
            if (args.length == 1) {
                level = 1;
            } else {
                level = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            level = 1;
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "No Item In Hand"));
            return;
        }

        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0]));

        if (enchant == null) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage",
                    Placeholder.parsed("command", "/enchant {enchantment} {level}")));
            return;
        }

        item.addUnsafeEnchantment(enchant, level);


    }
}
