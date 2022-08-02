package jeeper.essentials.listeners.punishments;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.Objects;

public class PreventJoin implements Listener {

    static DSLContext dslContext = Main.getPlugin().getDslContext();
    static Config config = Main.getPlugin().config();

    @EventHandler
    public void onBanJoin(AsyncPlayerPreLoginEvent e) {
        var banRecord =
                dslContext.select(Tables.PUNISHMENTS.IPADDRESS, Tables.PUNISHMENTS.PUNISHMENTREASON, Tables.PUNISHMENTS.PUNISHMENTEND)
                        .from(Tables.PUNISHMENTS)
                        .where(DSL.condition(dslContext.select(Tables.PUNISHMENTS.PUNISHMENTEND)
                                        .from(Tables.PUNISHMENTS)
                                        .where(Tables.PUNISHMENTS.PUNISHMENTTYPE.eq(Punishment.BAN.getPunishment()))
                                        .fetch(Tables.PUNISHMENTS.PUNISHMENTEND)
                                        .stream().filter(Objects::nonNull).anyMatch(time -> time.isAfter(LocalDateTime.now())))
                                .or(Tables.PUNISHMENTS.PUNISHMENTEND.isNull())
                                .and(Tables.PUNISHMENTS.PUNISHMENTTYPE.equalIgnoreCase(Punishment.BAN.getPunishment())))
                        .orderBy(Tables.PUNISHMENTS.PUNISHMENTEND.desc().nullsFirst()).limit(1).fetchOne();

        if (banRecord != null) {

            var address = e.getAddress().getHostAddress();

            if (address.equals(banRecord.get(Tables.PUNISHMENTS.IPADDRESS))) {
                String reason = banRecord.get(Tables.PUNISHMENTS.PUNISHMENTREASON);
                if (reason == null) {
                    e.kickMessage(permBanNoReasonMessage());
                } else {
                    e.kickMessage(permBanMessage(reason));
                }
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, e.kickMessage());

            }

            if(banRecord.value3() == null) {
                if (banRecord.value2() == null) {
                    e.kickMessage(permBanNoReasonMessage());
                } else {
                    e.kickMessage(permBanMessage(banRecord.value2()));
                }
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, e.kickMessage());
                return;

            }

            if (banRecord.value2() == null) { //no reason, use other message
                e.kickMessage(tempBanNoReasonMessage(banRecord.value3()));
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, e.kickMessage());
                return;
            }

            //if the ban time is passed, let the player join
            if (banRecord.value3().isBefore(LocalDateTime.now())) {
                return;
            }


            //send message with reason
            e.kickMessage(tempBanReasonMessage(banRecord.value3(), banRecord.value2()));
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, e.kickMessage());
        }
    }
    /**
     * @return the kick message for a permanent ban, with a reason
     */
    public static Component permBanNoReasonMessage() {
        String discordLink = MessageTools.getString(config, "Discord Link");
        return MessageTools.parseFromPath(config, "Punishment Header").append(Component.newline())
                .append(MessageTools.parseFromPath(config, "Perm Ban Message", Placeholder.component("discord",
                        Component.text(discordLink).clickEvent(ClickEvent.openUrl(discordLink)))));
    }

    /**
     * @return the kick message for a permanent ban
     */
    public static Component permBanMessage(String reason) {
        String discordLink = MessageTools.getString(config, "Discord Link");
        return MessageTools.parseFromPath(config, "Punishment Header").append(Component.newline())
                .append(MessageTools.parseFromPath(config, "Perm Ban With Reason", Placeholder.component("discord",
                        Component.text(discordLink).clickEvent(ClickEvent.openUrl(discordLink))),
                        Placeholder.component("reason", MessageTools.parseText(reason))));
    }

    /**
     * @return the kick message for a temp ban without a reason
     */
    public static Component tempBanNoReasonMessage(LocalDateTime banEnd) {
        String discordLink = MessageTools.getString(config, "Discord Link");

        return MessageTools.parseFromPath(config, "Punishment Header").append(Component.newline())
                .append(MessageTools.parseFromPath(config, "Ban Message", Placeholder.parsed("time", PunishmentTools.getPunishmentEndString(banEnd)),
                        Placeholder.component("discord", Component.text(discordLink).clickEvent(ClickEvent.openUrl(discordLink)))));
    }

    /**
     * @return the kick message for a temp ban with a reason
     */
    public static Component tempBanReasonMessage(LocalDateTime banEnd, String reason) {
        String discordLink = MessageTools.getString(config, "Discord Link");

        return MessageTools.parseFromPath(config, "Punishment Header").append(Component.newline())
                .append(MessageTools.parseFromPath(config, "Ban With Reason", Placeholder.parsed("reason", reason),
                        Placeholder.parsed("time", PunishmentTools.getPunishmentEndString(banEnd)),
                        Placeholder.component("discord", Component.text(discordLink).clickEvent(ClickEvent.openUrl(discordLink)))));
    }




}
