package jeeper.essentials.listeners.punishments;

import essentials.db.Tables;
import jeeper.essentials.Main;
import jeeper.essentials.commands.admin.punishments.PunishmentHistory;
import jeeper.essentials.commands.admin.punishments.Punishments;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jooq.DSLContext;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PunishmentMenuListener implements Listener {

    static Config config = Main.getPlugin().config();
    static DSLContext dslContext = Main.getPlugin().getDslContext();

    static Pattern timePattern = Pattern.compile("\\s[0-9]+?\\s");


    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (Punishments.openMenus.contains(e.getWhoClicked().getUniqueId())) {
            String punishment = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
            punishment = punishment.substring(0, punishment.indexOf(' '));
            punishmentMenu(e, Punishment.valueOf(punishment.toUpperCase()));
        }
        if (PunishmentHistory.openMenus.contains(e.getWhoClicked().getUniqueId())) {
            punishmentHistoryMenu(e);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e) {
        Punishments.openMenus.remove(e.getPlayer().getUniqueId());
        PunishmentHistory.openMenus.remove(e.getPlayer().getUniqueId());
    }

    private void punishmentHistoryMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        Component itemName = item.getItemMeta().displayName();
        if (itemName == null) {
            return;
        }

        Inventory inv = e.getClickedInventory();
        if (inv == null) {
            return;
        }

        if (item.getType().equals(Material.RED_CONCRETE) || item.getType().equals(Material.ORANGE_CONCRETE)
                || item.getType().equals(Material.YELLOW_CONCRETE) || item.getType().equals(Material.LIME_CONCRETE)
                || item.getType().equals(Material.GRAY_CONCRETE)) {

            ItemStack playerHead = inv.getItem(4);
            if (playerHead == null) {
                return;
            }
            Component playerHeadName = playerHead.getItemMeta().displayName();
            if (playerHeadName == null) {
                return;
            }

            String playerName = PlainTextComponentSerializer.plainText().serialize(playerHeadName).substring(7);

            String uuid = UUIDTools.getUuid(playerName);

            OfflinePlayer player = UUIDTools.getOfflinePlayer(e.getWhoClicked(), playerName);
            if (player == null) {
                return;
            }


            List<Component> lore = item.lore();
            if (lore == null) {
                return;
            }

            String punishmentType = PlainTextComponentSerializer.plainText().serialize(itemName);

            dslContext.deleteFrom(Tables.PUNISHMENTS)
                    .where(Tables.PUNISHMENTS.USERID.eq(DatabaseTools.getUserID(uuid))
                            .and(Tables.PUNISHMENTS.PUNISHMENTTYPE.eq(punishmentType))
                            .and(Tables.PUNISHMENTS.PUNISHMENTSTART
                                    .startsWith(DatabaseTools.stringToLocalDateTime(PlainTextComponentSerializer.plainText().serialize(lore.get(3)).substring(7))))
                    ).execute();
            try {
                Main.getPlugin().getJDA().getGuilds().forEach(guild -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(punishmentType + " Removed From History");
                    embedBuilder.addField("Player", player.getName(), true);
                    embedBuilder.addField("Revoked By", e.getWhoClicked().getName(), true);
                    embedBuilder.setColor(Color.BLUE);
                    embedBuilder.setThumbnail("https://minotar.net/helm/" + player.getName() + "/64");
                    Objects.requireNonNull(guild.getTextChannelById(config.get().getLong("Punishment Channel ID"))).sendMessage(" ").setEmbeds(embedBuilder.build()).queue();
                });
            } catch (NullPointerException exception) {
                Bukkit.getLogger().warning("The punishment channel or bot has not been set up yet correctly. Check config.yml");
            }



            Bukkit.getLogger().warning(e.getWhoClicked().getName() + " has removed a " + PlainTextComponentSerializer.plainText().serialize(itemName).toLowerCase() + " from " + playerName + "'s punishment history.");

            e.getWhoClicked().openInventory(PunishmentHistory.makeHistoryMenu(player, e.getWhoClicked()));
            PunishmentHistory.openMenus.add(e.getWhoClicked().getUniqueId());

        }
    }

    private void punishmentMenu(InventoryClickEvent e, Punishment punishment) {
        e.setCancelled(true);
        String playerName = PlainTextComponentSerializer.plainText().serialize(e.getView().title()).replace(punishment.getPunishment() + " Player ", "");

        OfflinePlayer punished = UUIDTools.getOfflinePlayer(e.getWhoClicked(), playerName);
        ItemStack clicked = e.getCurrentItem();
        if (punished == null || punished.getName() == null || clicked == null) {
            return;
        }

        String ip = "";

        if (punished.isOnline()) {
            Player player = (Player) punished;
            if (player.getAddress() != null) {
                ip = (player.getAddress().getAddress().getHostAddress());
            }
        }
        
        //custom time
        if (clicked.getType().equals(Material.NAME_TAG)) {
            PunishmentTools.customPunishment(punishment, punished, e.getWhoClicked());
            return;
        }
        
        List<Material> buttons = List.of(Material.RED_CONCRETE, Material.ORANGE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.GREEN_CONCRETE);
        for (Material button : buttons) {
            if (!clicked.getType().equals(button)) {
                continue;
            }
            
            List<Component> lore = clicked.lore();

            if (lore == null || lore.size() < 2) {
                return;
            }

            String reason = PlainTextComponentSerializer.plainText().serialize(lore.get(1)).replace(punishment.getPunishment() + " Reason: ", "");

            //if right click, edit time
            if (e.getClick().isRightClick()) {
                PunishmentTools.timeMenu(punishment, punished, e.getWhoClicked(), reason);
                return;
            }
            int timeNumber = -1; //if these values aren't changed set and player is muted/banned, its permanent
            String timeUnit = "";
            //get the time as a number
            if (lore.size() >= 3) {
                String secondLine = PlainTextComponentSerializer.plainText().serialize(lore.get(2));
                Matcher matcher = timePattern.matcher(secondLine);
                if (matcher.find()) {
                    timeNumber = Integer.parseInt(matcher.group().replace(" ", ""));
                    timeUnit = secondLine.substring(matcher.end());
                }
            }

            //null if permanent
            LocalDateTime endTime = null;

            if (timeNumber != -1 && !timeUnit.equals("")) {
                if (timeUnit.equalsIgnoreCase("Days")) {
                    endTime = LocalDateTime.now().plus(timeNumber, ChronoUnit.DAYS);
                } else if (timeUnit.equalsIgnoreCase("Hours")) {
                    endTime = LocalDateTime.now().plus(timeNumber, ChronoUnit.HOURS);
                } else if (timeUnit.equalsIgnoreCase("Minutes")) {
                    endTime = LocalDateTime.now().plus(timeNumber, ChronoUnit.MINUTES);
                }
            }
                PunishmentTools.addPunishmentToDB(e.getWhoClicked(), punishment, e.getWhoClicked().getUniqueId().toString(), ip, punished, LocalDateTime.now(), endTime, reason);
        }
        e.getWhoClicked().closeInventory();
    }

}
