package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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


        results.entrySet().iterator().forEachRemaining((map) -> {
            String value = map.getValue();
            String key = map.getKey();
            
            
            
            if (value == null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(key));

                String name = player.getName();
                if (name == null) {
                    results.remove(key);
                    return;
                }
                
                if (name.equalsIgnoreCase(args[0])){
                    results.replace(key, name);
                }
                else {
                    results.remove(key);
                }
            }
            else if (!PlainTextComponentSerializer.plainText().serialize(MessageTools.parseText(value)).equals(nickname)) {
                results.remove(key);
            }
        });

        List<Component> names = new ArrayList<>();
        List<String> uuids = results.keySet().stream().toList();

        uuids.forEach(uuid -> names.add(MessageTools.parseText(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(uuid))).getName())));
        
        if (results.size() == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Nickname Not In Use", Placeholder.parsed("nickname", nickname)));
            return;
        }
        
        Component separatedNames = Component.empty();
        for (int i = 0; i < names.size(); i++) {
            separatedNames = separatedNames.append(names.get(i));
            if (i != names.size()-1) {
                separatedNames = separatedNames.append(Component.text(", "));
            }
        }

        sender.sendMessage(MessageTools.parseFromPath(config, "Players With Name",
                Placeholder.parsed("nickname", nickname),
                Placeholder.component("players", separatedNames)));

    }
}
