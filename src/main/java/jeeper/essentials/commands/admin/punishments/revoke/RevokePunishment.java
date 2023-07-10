package jeeper.essentials.commands.admin.punishments.revoke;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class RevokePunishment {

    static DSLContext dslContext = Main.getPlugin().getDslContext();
    static Config config = Main.getPlugin().config();

    /**
     * Revokes a punishment
     * @param punishment The punishment to revoke
     * @param sender The sender of the command
     * @param playerName The arguments of the command (should be the player name)
     */
    public static void revoke(Punishment punishment, CommandSender sender, String playerName) {
        OfflinePlayer player = UUIDTools.getOfflinePlayer(sender, playerName);
        if (player == null) {
            return;
        }

        String uuid = player.getUniqueId().toString();
        
        // Get punishments of type punishment that are still going on
        var punishmentCondition =
                DSL.condition(dslContext.select(Tables.PUNISHMENTS.PUNISHMENTEND)
                                .from(Tables.PUNISHMENTS)
                                .where(Tables.PUNISHMENTS.PUNISHMENTTYPE.equalIgnoreCase(punishment.getPunishment())
                                        .and(Tables.PUNISHMENTS.USERID.eq(DatabaseTools.getUserID(uuid))))
                                .fetch(Tables.PUNISHMENTS.PUNISHMENTEND)
                                .stream().filter(Objects::nonNull).anyMatch(time -> time.isAfter(LocalDateTime.now())))
                        .or(Tables.PUNISHMENTS.PUNISHMENTEND.isNull())
                        .and(Tables.PUNISHMENTS.PUNISHMENTTYPE.equalIgnoreCase(punishment.getPunishment()))
                        .and(Tables.PUNISHMENTS.USERID.eq(DatabaseTools.getUserID(uuid)));

        var punishRecord = dslContext.selectFrom(Tables.PUNISHMENTS)
                .where(punishmentCondition).fetch();

        if (punishRecord.size() == 0) {
            if (punishment.equals(Punishment.BAN)) {
                sender.sendMessage(MessageTools.parseFromPath(config, "Unban Not Banned", Placeholder.parsed("player", playerName)));
            } else if (punishment.equals(Punishment.MUTE)) {
                sender.sendMessage(MessageTools.parseFromPath(config, "Unmute Not Muted", Placeholder.parsed("player", playerName)));
            }
            return;
        }

        Bukkit.getLogger().warning((sender.getName().equals("CONSOLE") ? "Console" : sender.getName()) + " has revoked " + playerName + "'s " + punishment.getPunishment() + " punishment");

        punishRecord.forEach(p -> {
            p.setPunishmentend(LocalDateTime.now());
            dslContext.executeUpdate(p, punishmentCondition);//UPDATE __ SET __ WHERE __
        });

        if (punishment.equals(Punishment.BAN)) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Player Unbanned", Placeholder.parsed("player", playerName)));
        } else {
            sender.sendMessage(MessageTools.parseFromPath(config, "Player Unmuted", Placeholder.parsed("player", playerName)));
        }

        //a switch to get all string values of punishments
        String punishmentString = switch(punishment) {
            case BAN -> "Unbanned";
            case MUTE -> "Unmuted";
            case KICK -> "Kick Revoked";
            case WARN -> "Warn Revoked";
        };


        try {
            Main.getPlugin().getJDA().getGuilds().forEach(guild -> {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle(punishmentString);
                embedBuilder.addField("Player", player.getName(), true);
                embedBuilder.addField("Revoked By", sender instanceof Player ? sender.getName() : "Console", true);
                embedBuilder.setColor(Color.CYAN);
                embedBuilder.setThumbnail("https://minotar.net/helm/" + player.getName() + "/64");
                Objects.requireNonNull(guild.getTextChannelById(config.get().getLong("Punishment Channel ID"))).sendMessage(" ").setEmbeds(embedBuilder.build()).queue();
            });
        } catch (NullPointerException e) {
            Bukkit.getLogger().warning("The punishment channel or bot has not been set up yet correctly. Check config.yml");
        }
    }
}
