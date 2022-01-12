package jeeper.essentials.commands;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.ConfigSetup;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.minimessage.Template;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jooq.DSLContext;
import org.jooq.Record1;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;

public class Report extends PluginCommand {

    private final ConfigSetup config = Main.getPlugin().config();
    private final DSLContext dslContext = Main.getPlugin().getDslContext();
    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getName() {
        return "report";
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length >= 2) {
            if (player.getName().equals(args[0])) {
                player.sendMessage(MessageTools.parseFromPath(config, "Report Yourself"));
                return;
            }

            int reporterID = DatabaseTools.getUserID(player.getUniqueId());

            if (reporterID == -1) {
                DatabaseTools.addUser(player.getUniqueId());
                reporterID = DatabaseTools.getUserID(player.getUniqueId());
            }

            OfflinePlayer reported = UUIDTools.checkNameAndUUID(player, args[0]);
            if (reported == null) {
                return;
            }

            int reportedID = DatabaseTools.getUserID(reported.getUniqueId());

            if (reportedID == -1) {
                DatabaseTools.addUser(reported.getUniqueId());
                reportedID = DatabaseTools.getUserID(reported.getUniqueId());
            }
            if (cooldown.containsKey(player.getUniqueId())) {
                long secondsLeft = cooldown.get(player.getUniqueId()) + 60000 - System.currentTimeMillis();
                if (secondsLeft > 0) {
                    player.sendMessage(MessageTools.parseFromPath(config, "Command Cooldown", Template.template("time", secondsLeft / 1000 + " seconds")));
                    return;
                }
                cooldown.remove(player.getUniqueId());
            } else {
                cooldown.put(player.getUniqueId(), System.currentTimeMillis());
            }

            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            dslContext.insertInto(Tables.REPORTS, Tables.REPORTS.REPORTERID, Tables.REPORTS.REPORTEDID, Tables.REPORTS.REPORTREASON, Tables.REPORTS.REPORTDATE)
                    .values(reporterID, reportedID, reason, LocalDateTime.now()).execute();

            Record1<Integer> intRecord = dslContext.select(Tables.REPORTS.REPORTID).from(Tables.REPORTS)
                    .where(Tables.REPORTS.REPORTERID.eq(reporterID)
                                    .and(Tables.REPORTS.REPORTEDID.eq(reportedID)
                                    .and(Tables.REPORTS.REPORTREASON.eq(reason)))).orderBy(Tables.REPORTS.REPORTDATE.desc()).limit(1).fetchOne();

            String reportNumberAsString = String.valueOf(intRecord == null ? "null" : intRecord.value1());

            player.sendMessage(MessageTools.parseFromPath(config, "Report Message"));

            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            for (Player p : players) {
                if (p.hasPermission("jeeper.reports.view") || p.isOp()) {
                    p.sendMessage(MessageTools.parseFromPath(config, "New Report",
                            Template.template("number", reportNumberAsString),
                            Template.template("reporter", player.getName()),
                            Template.template("reported", args[0]),
                            Template.template("reason", reason)));
                }
            }

            Bukkit.getLogger().info("[REPORT] Player " + reported.getName() + " has been reported by " + player.getName() + ". Reason: " + reason);

            try {
                Main.getPlugin().getJDA().getGuilds().forEach(guild -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Report #"+reportNumberAsString);
                    embedBuilder.addField("Player", args[0], true);
                    embedBuilder.addField("Reported By", player.getName(), true);
                    embedBuilder.addField("Reason", reason, false);
                    embedBuilder.setColor(Color.MAGENTA);
                    embedBuilder.setThumbnail("https://minotar.net/helm/" + reported.getName() + "/64");
                    Objects.requireNonNull(guild.getTextChannelById(config.get().getLong("Report Channel ID"))).sendMessage(" ").setEmbeds(embedBuilder.build()).queue();
                });
            } catch (NullPointerException exception) {
                Bukkit.getLogger().info("The report channel or bot has not been set up yet correctly. Check config.yml");
            }

        } else {
            player.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Template.template("command", "/report {player} {reason}")));
        }
    }
}
