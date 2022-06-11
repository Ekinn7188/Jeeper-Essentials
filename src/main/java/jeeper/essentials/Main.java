package jeeper.essentials;

import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.commands.tab.PluginTabCompleter;
import jeeper.essentials.database.SQLite;
import jeeper.essentials.lag.ClearLag;
import jeeper.essentials.log.LogFilter;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.config.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jooq.DSLContext;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Main extends JavaPlugin {
    private static Main plugin;
    private Config config;
    private DSLContext dslContext;
    private JDA jda;

    @Override
    public void onEnable() {

        initializeClasses();

        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new LogFilter());

        runBot();
        TabMenu.updateTabLoop();

        ClearLag.clearLagLoop();
    }

    @Override
    public void onLoad() {
        plugin = this;
        startFileSetup();
        try {
           dslContext = SQLite.databaseSetup(getPlugin().getDataFolder().getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void runBot() {
        String token = config.get().getString("Discord Bot Token");

        try{
            jda = JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            Bukkit.getLogger().warning("Failed to connect to Discord. Try checking your bot token in config.yml");
        }

    }

    private void initializeClasses(){
        String packageName = Main.getPlugin().getClass().getPackage().getName();
        //load Listeners in net.dirtlands.listeners
        for(Class<?> listenerClass :new Reflections(packageName +".listeners").getSubTypesOf(Listener.class)) {
            try {
                Listener listener = (Listener) listenerClass.getDeclaredConstructor().newInstance(); //must have empty constructor
                Main.getPlugin().getServer().getPluginManager().registerEvents(listener, Main.getPlugin());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }


        //load PluginCommands in net.dirtlands.commands
        for(Class<? extends PluginCommand> commandClass :new Reflections(packageName +".commands").getSubTypesOf(PluginCommand.class)) {
            try {
                PluginCommand pluginCommand = commandClass.getDeclaredConstructor().newInstance();
                Objects.requireNonNull(Main.getPlugin().getCommand(pluginCommand.getName())).setExecutor(pluginCommand);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        //load PluginTabCompleters in net.dirtlands.commands.tab
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
