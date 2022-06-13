package jeeper.essentials.commands.tab;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnchantCompleter extends PluginTabCompleter{
    @Override
    public List<String> getNames() {
        return List.of("enchant");
    }

    @Override
    public List<String> tabCompleter(Player player, @NotNull String[] args) {

        if (args.length == 1) {
            List<Enchantment> enchantments = new ArrayList<>(Arrays.asList(Enchantment.values()));

            List<String> names = new ArrayList<>();

            enchantments.forEach(enchantment -> names.add(enchantment.key().value()));

            return names;

        }



        return new ArrayList<>();
    }
}
