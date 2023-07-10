package jeeper.essentials.commands.admin.punishments;

import essentials.db.Tables;
import essentials.db.tables.records.PunishmentsRecord;
import jeeper.essentials.Main;
import jeeper.essentials.commands.Permission;
import jeeper.essentials.commands.PluginCommand;
import jeeper.essentials.database.DatabaseTools;
import jeeper.essentials.listeners.punishments.Punishment;
import jeeper.essentials.tools.GUITools;
import jeeper.essentials.tools.ItemTools;
import jeeper.essentials.tools.UUIDTools;
import jeeper.utils.MessageTools;
import jeeper.utils.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PunishmentHistory extends PluginCommand {
    static DSLContext dslContext = Main.getPlugin().getDslContext();
    static Config config = Main.getPlugin().config();
    public static ArrayList<UUID> openMenus = new ArrayList<>();

    @Override
    public String getName() {
        return "history";
    }

    @Override
    protected Permission getPermissionType() {
        return Permission.HISTORY;
    }

    @Override
    public boolean isRequiresPlayer() {
        return false;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageTools.parseFromPath(config, "Correct Usage", Placeholder.parsed("command", "/history {player}")));
            return;
        }

        OfflinePlayer punished = UUIDTools.getOfflinePlayer(sender, args[0]);
        if (punished == null) {
            return;
        }

        if (sender instanceof Player p) {
            Inventory menu = makeHistoryMenu(punished, p);
            p.openInventory(menu);
            openMenus.add(p.getUniqueId());
        }

    }

    public static Inventory makeHistoryMenu(OfflinePlayer player, HumanEntity viewer){
        var history = dslContext.selectFrom(Tables.PUNISHMENTS)
                .where(Tables.PUNISHMENTS.USERID.eq(DatabaseTools.getUserID(player.getUniqueId())))
                .orderBy(DSL.case_(Tables.PUNISHMENTS.PUNISHMENTTYPE)
                                .when(Punishment.BAN.getPunishment(), 1)
                                .when(Punishment.MUTE.getPunishment(), 2)
                                .when(Punishment.KICK.getPunishment(), 3)
                                .when(Punishment.WARN.getPunishment(), 4)
                                .else_(5).asc()
                        , Tables.PUNISHMENTS.PUNISHMENTEND.desc().nullsLast()).limit(28).fetch();

        assert player.getName() != null; //checked in checkNameAndUUID

        Inventory inv = Bukkit.createInventory(viewer, 54, MessageTools.parseText("<dark_red>" + player.getName() + "<red>'s Punishment History"));

        GUITools.addBorder(inv, Material.BLACK_STAINED_GLASS_PANE);

        ItemStack playerHead = ItemTools.getHead(player);
        inv.setItem(4, ItemTools.createGuiItem(playerHead, playerHead.getType(),
                MessageTools.parseText("&cPlayer &4<player>", Placeholder.parsed("player", player.getName())),
                1));

        for (PunishmentsRecord punishment : history) {
            inv.addItem(makeHistoryItem(player, punishment));
        }

        return inv;
    }

    private static ItemStack makeHistoryItem(OfflinePlayer player, PunishmentsRecord punishment){
        assert player.getName() != null; //checked in checkNameAndUUID

        String punisherUUID = dslContext.select(Tables.USERS.USERUUID).from(Tables.USERS).where(Tables.USERS.USERID.eq(punishment.getPunisherid())).fetchOne(Tables.USERS.USERUUID);
        String punisherName = "Console";
        if (punisherUUID != null) {
            punisherName = Bukkit.getOfflinePlayer(UUID.fromString(punisherUUID)).getName();
        }
        if (punisherName == null) {
            punisherName = "Console";
        }

        String punishmentType = punishment.getPunishmenttype();
        String reason = punishment.getPunishmentreason();
        String start = DatabaseTools.localDateTimeToString(punishment.getPunishmentstart());
        String end = DatabaseTools.localDateTimeToString(punishment.getPunishmentend());

        Map<List<String>, Material> punishmentInfo = switch(Punishment.valueOf(punishmentType.toUpperCase().replace(" ", "_"))) {
            case BAN -> Map.of(List.of("<red>", "<dark_red>"), Material.RED_CONCRETE);
            case MUTE -> Map.of(List.of("<#ff8c00>", "<#cc5500>"), Material.ORANGE_CONCRETE);
            case KICK -> Map.of(List.of("<#ffe205>", "<gold>"), Material.YELLOW_CONCRETE);
            case WARN -> Map.of(List.of("<#39ff14>", "<#31b81f>"), Material.LIME_CONCRETE);
        };

        List<String> colors = punishmentInfo.entrySet().iterator().next().getKey();
        String lightColor = colors.get(0);
        String darkColor = colors.get(1);
        Material material = punishmentInfo.entrySet().iterator().next().getValue();

        List<Component> lore = new ArrayList<>();
        lore.add(MessageTools.parseText(" "));
        lore.add(MessageTools.parseText(lightColor + "Reason: "+ darkColor +"<reason>", Placeholder.parsed("reason", reason == null ? "None" : reason)));
        lore.add(MessageTools.parseText(lightColor + "Punished By: " + darkColor +"<punisher>", Placeholder.parsed("punisher", punisherName)));
        lore.add(MessageTools.parseText(lightColor + "Start: " + darkColor +"<start>", Placeholder.parsed("start", start)));
        if (!punishment.getPunishmenttype().equals(Punishment.WARN.getPunishment()) && !punishment.getPunishmenttype().equals(Punishment.KICK.getPunishment())) {
            if (end == null) {
                lore.add(MessageTools.parseText( "<red>End: <end>", Placeholder.parsed("end", "Permanent")));
            } else {
                if (punishment.getPunishmentend().isAfter(LocalDateTime.now())) {
                    lore.add(MessageTools.parseText("<red>End: <end>", Placeholder.parsed("end", end)));
                } else {
                    lore.add(MessageTools.parseText("<green>End: <end>", Placeholder.parsed("end", end)));
                }
            }
        }
        lore.add(MessageTools.parseText(" "));
        lore.add(MessageTools.parseText(darkColor + "Click" + lightColor + " to clear from " + darkColor + player.getName() + lightColor + "'s record"));

        return ItemTools.createGuiItem(material, MessageTools.parseText(lightColor + punishmentType), 1, lore.toArray(new Component[0]));
    }


}
