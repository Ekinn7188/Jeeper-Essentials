package jeeper.essentials.listeners;

import essentials.db.Tables;
import essentials.db.tables.records.ReportsRecord;
import jeeper.essentials.Main;
import jeeper.essentials.commands.admin.ViewReports;
import jeeper.utils.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jooq.DSLContext;

import java.awt.*;
import java.util.Objects;

public class ReportListener implements Listener {

    private static final DSLContext dslContext = Main.getPlugin().getDslContext();
    private static final Config config = Main.getPlugin().config();

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (!ViewReports.reportsMenu.containsKey(e.getWhoClicked().getUniqueId())) {
            return;
        }
        if (e.getClickedInventory() == null) {
            return;
        }
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }
        e.setCancelled(true);
        if (e.getCurrentItem() == null) {
            return;
        }


        if (e.getCurrentItem().getType().equals(Material.MAGENTA_CONCRETE)) {
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            Component name = meta.displayName();
            if (name == null) {
                return;
            }
            int id;
            try {
                id = Integer.parseInt(PlainTextComponentSerializer.plainText().serialize(name).split("#")[1]);
            } catch (NumberFormatException ex) {
                return;
            }

            ReportsRecord report = dslContext.selectFrom(Tables.REPORTS).where(Tables.REPORTS.REPORTID.eq(id)).fetchOne();

            dslContext.deleteFrom(Tables.REPORTS).where(Tables.REPORTS.REPORTID.eq(id)).execute();

            try {
                Main.getPlugin().getJDA().getGuilds().forEach(guild -> {
                    MessageChannel channel = guild.getTextChannelById(config.get().getLong("Report Channel ID"));

                    MessageHistory.getHistoryFromBeginning(Objects.requireNonNull(channel)).queue(message -> message.getRetrievedHistory().forEach(msg -> msg.getEmbeds().forEach(embed -> {
                        String title = embed.getTitle();
                        if (title == null) {
                            return;
                        }
                        if (title.contains("Report #" + id) && !title.contains("INVESTIGATED")) {
                            EmbedBuilder builder = new EmbedBuilder(embed);
                            builder.setTitle("Report #" + id + " (INVESTIGATED)");
                            builder.setColor(Color.GREEN);
                            msg.editMessageEmbeds(builder.build()).queue();
                        }
                    })));
                });
            } catch (NullPointerException exception) {
                Bukkit.getLogger().info("The report channel or bot has not been set up yet correctly. Check config.yml");
            }

            e.setCurrentItem(new ItemStack(Material.AIR));
            Inventory inv = ViewReports.getReportsMenu();
            e.getWhoClicked().openInventory(inv);
            if (!ViewReports.reportsMenu.containsKey(e.getWhoClicked().getUniqueId())) {
                ViewReports.reportsMenu.put(e.getWhoClicked().getUniqueId(), inv);
            }
        }



    }

    @EventHandler
    public void menuClose(InventoryCloseEvent e) {
        ViewReports.reportsMenu.remove(e.getPlayer().getUniqueId());
    }

}
