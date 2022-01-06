package jeeper.essentials.listeners;

import essentials.db.Tables;
import io.papermc.paper.event.player.AsyncChatEvent;
import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.admin.MuteChat;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.listeners.punishments.PunishmentTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.HashMap;
import java.util.UUID;

public class Chat implements Listener {

    DSLContext dslContext = Main.getPlugin().getDslContext();
    private static final ConfigSetup config = Main.getPlugin().config();

    private static final HashMap<UUID, Cooldown> cooldown = new HashMap<>();

    //if event with lower than default priority is canceled, chat won't send
    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncChatEvent e){


        e.setCancelled(true);
        Player player = e.getPlayer();

        //muted chat
        if (MuteChat.chatMuted){
            if (!e.getPlayer().hasPermission(Permission.BYPASSCHAT.getName())){
                Bukkit.broadcast(MessageTools.parseFromPath(config, "Chat Is Muted"));
                return;
            }
        }

        if (PunishmentTools.checkMuted(player)){
            return;
        }


        //chat cooldown
        String messageString = PlainTextComponentSerializer.plainText().serialize(e.message());

        if (PlainTextComponentSerializer.plainText().serialize(MessageTools.parseText(messageString)).equals("")) {
            return;
        }

        if (!player.hasPermission(Permission.COOLDOWN.getName())) {
            if (cooldown.containsKey(player.getUniqueId())) {
                Cooldown cooldownInfo = cooldown.get(player.getUniqueId());
                long secondsLeft = cooldownInfo.getCooldown() + 2500 - System.currentTimeMillis();
                if (secondsLeft > 0) {
                    player.sendMessage(MessageTools.parseFromPath(config, "Sending Messages Too Fast", Template.template("message", messageString)));
                    return;
                }
            }
            Cooldown newCooldown = new Cooldown(messageString, System.currentTimeMillis());
            cooldown.put(player.getUniqueId(), newCooldown);
        }




        //if the player has permission to use a chat color and has one set, use it
        Component message = MessageTools.parseText(messageString);
        String chatcolor = DatabaseTools.firstString(dslContext.select(Tables.USERS.CHATCOLOR)
                .from(Tables.USERS).where(Tables.USERS.USERID.eq(DatabaseTools.getUserID(e.getPlayer().getUniqueId()))).fetchAny());
        if (player.hasPermission("dirtlands.chat.color") && chatcolor != null ){
            message = MessageTools.parseText(chatcolor + messageString);
        }

        //get prefix and suffix
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        assert user != null;
        String prefix = user.getCachedData().getMetaData().getPrefix();
        String suffix = user.getCachedData().getMetaData().getSuffix();

        //compile everything into a message to send
        Component replacedText = MessageTools.parseFromPath(config, "Chat Style", Template.template("prefix", MessageTools.parseText(prefix == null ? "" : prefix + " ")),
                Template.template("player", e.getPlayer().displayName()), Template.template("suffix", MessageTools.parseText(suffix == null ? "" : " " + suffix)),
                Template.template("message", message));


        //get any urls and make them clickable
        for (String url : MessageTools.fetchURLs(messageString)) {
            replacedText = replacedText.clickEvent(ClickEvent.openUrl(url));
        }

        //get userid to deal with ignored players
        int userid = DatabaseTools.getUserID(player.getUniqueId());
        if (userid == -1){
            DatabaseTools.addUser(player.getUniqueId());
            userid = DatabaseTools.getUserID(player.getUniqueId());
        }

        //send message to all players who don't have sender ignored
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            int receiverID = DatabaseTools.getUserID(onlinePlayer.getUniqueId());
            if (receiverID == -1){
                DatabaseTools.addUser(onlinePlayer.getUniqueId());
                receiverID = DatabaseTools.getUserID(onlinePlayer.getUniqueId());
            }

            boolean ignored = dslContext.fetchExists(dslContext.select().from(Tables.IGNOREDPLAYERS)
                    .where(Tables.IGNOREDPLAYERS.USERID.eq(receiverID), Tables.IGNOREDPLAYERS.IGNOREDPLAYERID.eq(userid)));

            //if player isn't ignored, dont send message
            if (!ignored) {
                onlinePlayer.sendMessage(replacedText);
            }
        }

        Bukkit.getLogger().info(PlainTextComponentSerializer.plainText().serialize(replacedText));


    }
}

//stores UUID and message sent for each player
class Cooldown {
    String message;
    long cooldown;

    public Cooldown(@NotNull String message, long cooldown){
        this.message = message;
        this.cooldown = cooldown;
    }

    public @NotNull String getUUID(){
        return message;
    }

    public long getCooldown(){
        return cooldown;
    }

    public void setCooldown(long cooldown){
        this.cooldown = cooldown;
    }

    public void setMessage(String message){
        this.message = message;
    }
}