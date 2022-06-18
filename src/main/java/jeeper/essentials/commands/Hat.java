package jeeper.essentials.commands;

import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat extends PluginCommand {
    @Override
    public String getName() {
        return "hat";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.HAT;
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack helmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(player.getInventory().getItemInMainHand());
        player.getInventory().setItemInMainHand(helmet);

        player.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Hat Set"));
    }
}
