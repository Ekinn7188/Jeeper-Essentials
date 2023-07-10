package jeeper.essentials;

import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.commands.tab.PluginTabCompleter;
import jeeper.essentials.database.SQLite;
import jeeper.essentials.lag.ClearLag;
import jeeper.essentials.log.LogFilter;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.ParserDirective;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;
import org.jooq.DSLContext;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends JavaPlugin {
    private static Main plugin;
    private Config config;
    private DSLContext dslContext;
    private JDA jda;

    @Override
    public void onEnable() {

        initializeClasses();

        customMiniMessage();
        
        runBot();
        TabMenu.updateTabLoop();

        ClearLag.clearLagLoop();
    }

    @Override
    public void onLoad() {
        plugin = this;

        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new LogFilter());

        startFileSetup();
        try {
           dslContext = SQLite.databaseSetup(getPlugin().getDataFolder().getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void customMiniMessage() {
        
        var configSection = config.get().getConfigurationSection("variables");
        
        if (Objects.isNull(configSection)) {
            // No custom variables
            return;
        }
        
        ArrayList<TagResolver.Single> tags = new ArrayList<>();
        for (String key : configSection.getKeys(false)) {
            String value = configSection.getString(key);
            if (Objects.isNull(value)) {
                // config is written wrong
                continue;
            }
            tags.add(TagResolver.resolver(key, Tag.inserting(MessageTools.parseText(value))));
        }

        MiniMessage parser = MiniMessage.builder()
                .editTags(t -> t.resolvers(tags))
                .build();
        
        MessageTools.setMiniMessage(parser);
    }

    private void runBot() {
        String token = config.get().getString("Discord Bot Token");

        try{
            jda = JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build().awaitReady();
        } catch (InvalidTokenException | InterruptedException e) {
            Bukkit.getLogger().warning("Failed to connect to Discord. Try checking your bot token in config.yml");
        }

    }

    private void initializeClasses(){
        String packageName = Main.getPlugin().getClass().getPackage().getName();
        //load Listeners in jeeper.essentials.listeners
        for(Class<?> listenerClass :new Reflections(packageName +".listeners").getSubTypesOf(Listener.class)) {
            try {
                Listener listener = (Listener) listenerClass.getDeclaredConstructor().newInstance(); //must have empty constructor
                Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }


        //load PluginCommands in jeeper.essentials.commands
        for(Class<? extends PluginCommand> commandClass :new Reflections(packageName +".commands").getSubTypesOf(PluginCommand.class)) {
            try {
                PluginCommand pluginCommand = commandClass.getDeclaredConstructor().newInstance();
                Objects.requireNonNull(Main.getPlugin().getCommand(pluginCommand.getName())).setExecutor(pluginCommand);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        //load PluginTabCompleters in jeeper.essentials.commands.tab
        for(Class<? extends PluginTabCompleter> completerClass :new Reflections(packageName +".commands.tab").getSubTypesOf(PluginTabCompleter .class)) {
            try {
                PluginTabCompleter tabCompleter = completerClass.getDeclaredConstructor().newInstance();
                for (String commandName : tabCompleter.getNames()) {
                    Objects.requireNonNull(Main.getPlugin().getCommand(commandName)).setTabCompleter(tabCompleter);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

        }

    }

    private void startFileSetup() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        /*


        config.yml


        */

        config = new Config("config", "Jeeper-Essentials");
        config.readDefaults(this, "config.yml");
        config.get().options().copyDefaults(true);
        config.get().options().parseComments(true);
        config.save();

        //if making another file, add it to /essentials reload
    }

    public DSLContext getDslContext(){
        return dslContext;
    }
    public Config config() {
        return config;
    }
    public static Main getPlugin() {
        return plugin;
    }
    public JDA getJDA() {
        return jda;
    }
}
