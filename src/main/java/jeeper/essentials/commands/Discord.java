package jeeper.essentials.commands;

import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.command.CommandSender;

public class Discord extends PluginCommand{
    public static final ConfigSetup config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "discord";
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String discordLink = MessageTools.getString(config, "Discord Link");
        sender.sendMessage(MessageTools.parseFromPath(config, "Discord Message", Template.template("discord", MessageTools.parseText(discordLink).clickEvent(ClickEvent.openUrl(discordLink)))));
    }
}
