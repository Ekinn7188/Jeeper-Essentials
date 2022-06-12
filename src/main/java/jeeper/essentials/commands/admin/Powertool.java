package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class Powertool extends PluginCommand {

    @Override
    public String getName() {
        return "powertool";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.POWERTOOL;
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Correct Usage", Placeholder.parsed("command", "/powertool {command}")));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s).append(" ");
        }
        String command = sb.toString().trim();

        ItemStack item = player.getInventory().getItemInMainHand();

        NamespacedKey key = new NamespacedKey(Main.getPlugin(), "powertool");

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, command);
        item.setItemMeta(meta);

        player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Powertool Created",
                Placeholder.parsed("command", command)));



    }
}
