package jeeper.essentials.commands.tab;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PowertoolTabCompleter extends PluginTabCompleter {

    @Override
    public List<String> getNames() {
        return List.of("powertool");
    }

    @Override
    public List<String> tabCompleter(Player player, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("remove");
        } else return Collections.emptyList();
    }

}
