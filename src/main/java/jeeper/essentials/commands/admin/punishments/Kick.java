package jeeper.essentials.commands.admin.punishments;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class Kick extends PluginCommand {
    ConfigSetup config = Main.getPlugin().config();
    DSLContext dslContext = Main.getPlugin().getDslContext();

    @Override
    public String getName() {
        return "kick";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.KICK;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Template.template("command", "/kick {player} {reason (optional)}")));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            return;
        }
        try{
            final String reason;
            if (args.length == 1) {
                player.kick(MessageTools.parseFromPath(config, "Punishment Header").append(Component.newline())
                        .append(MessageTools.parseFromPath(config, "Kick Message")));
                reason = null;
            } else {
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                player.kick(MessageTools.parseFromPath(config, "Punishment Header").append(Component.newline())
                        .append(MessageTools.parseFromPath(config, "Kick With Reason", Template.template("reason", reason))));
            }

            int punisherID = DatabaseTools.getUserID(sender instanceof Player ? String.valueOf(((Player) sender).getUniqueId()) : "Console");

            dslContext.insertInto(Tables.PUNISHMENTS)
                    .set(Tables.PUNISHMENTS.USERID, DatabaseTools.getUserID(player.getUniqueId()))
                    .set(Tables.PUNISHMENTS.PUNISHERID, punisherID == -1 ? null : punisherID)
                    .set(Tables.PUNISHMENTS.PUNISHMENTTYPE, Punishment.KICK.getPunishment())
                    .set(Tables.PUNISHMENTS.PUNISHMENTREASON, reason)
                    .set(Tables.PUNISHMENTS.PUNISHMENTSTART, LocalDateTime.now())
                    .set(Tables.PUNISHMENTS.PUNISHMENTEND, LocalDateTime.now())
                    .execute();
            Bukkit.getLogger().warning(player.getName() + " has been kicked by " +
                    (sender.getName().equals("CONSOLE") ? sender.getName().toLowerCase() : sender.getName()) + (reason == null ? "" : " for " + reason));
            try{
                Main.getPlugin().getJDA().getGuilds().forEach(guild -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Player kicked");
                    embedBuilder.addField("Player", player.getName(), true);
                    embedBuilder.addField("Punished By", sender instanceof Player ? sender.getName() : "Console", true);
                    embedBuilder.addField("Reason: ", reason == null ? "none" : reason, false);
                    embedBuilder.setColor(Color.ORANGE);
                    embedBuilder.setThumbnail("https://minotar.net/helm/" + player.getName() + "/64");
                    Objects.requireNonNull(guild.getTextChannelById(config.get().getLong("Punishment Channel ID"))).sendMessage(" ").setEmbeds(embedBuilder.build()).queue();
                });
            } catch (NullPointerException exception) {
                Bukkit.getLogger().info("The punishment channel or bot has not been set up yet correctly. Check config.yml");
            }

        } catch (AssertionError e) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Player Is Offline", Template.template("player", args[0])));
        }


    }
}
