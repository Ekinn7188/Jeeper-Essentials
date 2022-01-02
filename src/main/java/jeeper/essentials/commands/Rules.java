package jeeper.essentials.commands;

import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import org.bukkit.command.CommandSender;

public class Rules extends PluginCommand {
    ConfigSetup config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "rules";
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        config.get().getStringList("Rules").forEach(s -> sender.sendMessage(MessageTools.parseText(s)));
    }
}
