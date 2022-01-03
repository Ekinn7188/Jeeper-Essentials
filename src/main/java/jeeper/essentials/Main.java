package jeeper.essentials;

import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.commands.tab.PluginTabCompleter;
import jeeper.essentials.database.SQLite;
import jeeper.essentials.log.LogColor;
import jeeper.essentials.log.LogFilter;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.config.ConfigSetup;
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
    private ConfigSetup config;
    private DSLContext dslContext;
    private JDA jda;

    @Override
    public void onEnable() {

        initializeClasses();

        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        coreLogger.addFilter(new LogFilter());

        runBot();
        TabMenu.updateTabLoop();
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
            e.printStackTrace();
            Bukkit.getLogger().info(LogColor.RED + "Failed to connect to Discord. Try checking your bot token in config.yml" + LogColor.RESET);
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

        String header = """
                ############################################################
                # +------------------------------------------------------+ #
                # |                   Jeep's Essentials                  | #
                # +------------------------------------------------------+ #
                ############################################################
                                
                Developed by: Jeeper_ (Jeeper#6808)
                                
                Color Choices:
                   Hex Code Example: <#FFFFFF>text</#FFFFFF>
                   Make sure to surround the text with its color and make sure it ends with a /!!
                                
                   Minecraft Color Code Example: &aI'm typing in red!
                   This website has all the codes! https://www.digminecraft.com/lists/color_list_pc.php
                            
                    Extra info on text formatting: https://docs.adventure.kyori.net/minimessage.html#the-components
                                
                                
                ******* /essentials reload to reload files *******
                """;

        /*


        config.yml


        */

        config = new ConfigSetup("config", "Jeeper-Essentials");
        config.get().options().header(header);
        config.readDefaults(this, "config.yml");
        config.get().options().copyDefaults(true);
        config.get().options().copyHeader(true);
        config.save();

        //if making another file, add it to /dirtlands reload
    }

    public DSLContext getDslContext(){
        return dslContext;
    }
    public ConfigSetup config() {
        return config;
    }
    public static Main getPlugin() {
        return plugin;
    }
    public JDA getJDA() {
        return jda;
    }
}
