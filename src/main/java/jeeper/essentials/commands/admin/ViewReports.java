package jeeper.essentials.commands.admin;

import essentials.db.Tables;
import essentials.db.tables.records.ReportsRecord;
import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.tools.GUITools;
import jeeper.essentials.tools.ItemTools;
import jeeper.utils.MessageTools;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ViewReports extends PluginCommand {

    private static final DSLContext dslContext = Main.getPlugin().getDslContext();
    public static final Map<UUID, Inventory> reportsMenu = new HashMap<>();

    @Override
    public String getName() {
        return "reports";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.VIEW_REPORT;
    }

    @Override
    public void execute(Player player, String[] args) {
        Inventory inv = getReportsMenu();
        player.openInventory(inv);
        reportsMenu.put(player.getUniqueId(), inv);
    }

    public static Inventory getReportsMenu() {
        var history = dslContext.selectFrom(Tables.REPORTS)
                .orderBy(Tables.REPORTS.REPORTDATE.asc()).limit(28).fetch();

        Inventory inv = Bukkit.createInventory(null, 54, MessageTools.parseText("<red>Uninvestigated Reports"));

        GUITools.addBorder(inv, Material.BLACK_STAINED_GLASS_PANE);

        for (ReportsRecord report : history) {
            String uuidOfReporter = DatabaseTools.getUserUUID(report.component2());
            OfflinePlayer reporter = Bukkit.getOfflinePlayer(UUID.fromString(uuidOfReporter));
            String uuidOfReported = DatabaseTools.getUserUUID(report.component3());
            OfflinePlayer reported = Bukkit.getOfflinePlayer(UUID.fromString(uuidOfReported));

            ArrayList<Component> lore = new ArrayList<>();

            lore.add(MessageTools.parseText(""));
            lore.add(MessageTools.parseText("<dark_purple>Player: <light_purple>" + (reported.getName() == null ? "Unknown" : reported.getName())));
            lore.add(MessageTools.parseText("<dark_purple>Reported by: <light_purple>" + (reporter.getName() == null ? "Unknown" : reporter.getName())));
            lore.add(MessageTools.parseText("<dark_purple>Date Reported: <light_purple>" + DatabaseTools.localDateTimeToString(report.component5())));
            lore.add(MessageTools.parseText(""));

            String reason = report.component4();

            if (reason.length() > 32) {
                lore.add(MessageTools.parseText("<dark_purple>Reason: <light_purple>" + reason.substring(0, 32)));
                reason = reason.substring(32);
                //add every 40 characters to the arraylist
                for (int i = 0; i < reason.length(); i += 40) {
                    lore.add(MessageTools.parseText("<light_purple>" + reason.substring(i, Math.min(i + 40, reason.length()))));
                }
            } else {
                lore.add(MessageTools.parseText("<dark_purple>Reason: <light_purple>" + reason));
            }


            lore.add(MessageTools.parseText(""));
            lore.add(MessageTools.parseText("<dark_purple>Click <light_purple>to mark the report as <dark_purple>fully investigated"));


            ItemStack item = ItemTools.createGuiItem(Material.MAGENTA_CONCRETE, MessageTools.parseText("<dark_purple>Report #" + report.component1()),
                    1, lore.toArray(new Component[0]));

            inv.addItem(item);
        }

        return inv;
    }
}
