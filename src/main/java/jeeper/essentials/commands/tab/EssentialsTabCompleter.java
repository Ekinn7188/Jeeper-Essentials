package jeeper.essentials.commands.tab;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EssentialsTabCompleter extends PluginTabCompleter {

    @Override
    public List<String> getNames() {
        return List.of("essentials");
    }

    @Override
    public List<String> tabCompleter(Player player, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        completions.add("reload");

        return completions;
    }
}
