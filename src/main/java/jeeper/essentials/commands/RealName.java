package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.*;

public class RealName extends ChatColor {
    private final Config config = Main.getPlugin().config();

    @Override
    public String getName() {
        return "realname";
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //nickname without any special symbols like <green> or &b
        String nickname = PlainTextComponentSerializer.plainText().serialize(MessageTools.parseText(String.join(" ", args)));

        Map<String, String> results = Main.getPlugin().getDslContext().select(Tables.USERS.USERNICKNAME, Tables.USERS.USERUUID)
                .from(Tables.USERS).fetch().intoMap(Tables.USERS.USERUUID, Tables.USERS.USERNICKNAME);

        if (results.size() == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Nickname Not In Use", Placeholder.parsed("nickname", nickname)));
            return;
        }


        results.entrySet().iterator().forEachRemaining((map) -> {
            String value = map.getValue();
            String key = map.getKey();

            if (value == null || !PlainTextComponentSerializer.plainText().serialize(MessageTools.parseText(value)).equals(nickname)) {
                results.remove(key);
            }
        });

        List<Component> names = new ArrayList<>();
        List<String> uuids = results.keySet().stream().toList();

        uuids.forEach(uuid -> names.add(MessageTools.parseText(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))).getName())));

        if (results.size() > 1) {
            Component seperatedNames = Component.empty();
            for (int i = 0; i < names.size(); i++) {
                seperatedNames = seperatedNames.append(names.get(i));
                if (i != names.size()-1) {
                    seperatedNames = seperatedNames.append(Component.text(", "));
                }
            }

            sender.sendMessage(MessageTools.parseFromPath(config, "Players With Name",
                    Placeholder.parsed("nickname", nickname),
                    Placeholder.component("players", seperatedNames)));
            return;
        }

        sender.sendMessage(MessageTools.parseFromPath(config, "Players With Name",
                Placeholder.parsed("nickname", nickname),
                Placeholder.component("player", names.get(0))));




    }
}
