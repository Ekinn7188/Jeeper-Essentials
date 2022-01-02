package jeeper.essentials.listeners;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tabscoreboard.TabMenu;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jooq.DSLContext;

public class JoinAndLeave implements Listener {

    private static final ConfigSetup config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @EventHandler
    public void onJoin(PlayerJoinEvent e){

        int userID = DatabaseTools.getUserID(e.getPlayer().getUniqueId());

        if (userID == -1) {
            DatabaseTools.addUser(e.getPlayer().getUniqueId());
            userID = DatabaseTools.getUserID(e.getPlayer().getUniqueId());
        }

        //update player display name (it doesn't save when the server restarts!)
        String nickname = DatabaseTools.firstString(dslContext.select(Tables.USERS.USERNICKNAME).from(Tables.USERS).where(Tables.USERS.USERID.eq(userID)).fetchAny());
        if (nickname != null){
            e.getPlayer().displayName(MessageTools.parseText(nickname));
        }
        //update tab menu
        TabMenu.updateTab();
        if (!e.getPlayer().hasPlayedBefore()){
            e.joinMessage(MessageTools.parseFromPath(config, "First Join Message", Template.template("player", e.getPlayer().displayName()),
                    Template.template("number", String.valueOf(userID))));
        } else {
            e.joinMessage(MessageTools.parseFromPath(config, "Join Message", Template.template("player", e.getPlayer().displayName())));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        e.quitMessage(MessageTools.parseFromPath(config,"Leave Message", Template.template("player", e.getPlayer().displayName())));
    }

}