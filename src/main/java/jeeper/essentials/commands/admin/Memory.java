package jeeper.essentials.commands.admin;

import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;

public class Memory extends PluginCommand {
    @Override
    public String getName() {
        return "memory";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.MEMORY;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Runtime r = Runtime.getRuntime();
        long memUsed = (r.totalMemory() - r.freeMemory()) / 1048576;
        long memMax = r.maxMemory() / 1073741824;

        sender.sendMessage(MessageTools.parseFromPath(Main.getPlugin().config(), "Memory Usage", Template.template("memory", String.valueOf(memUsed)),
                Template.template("max", String.valueOf(memMax))));
    }
}
