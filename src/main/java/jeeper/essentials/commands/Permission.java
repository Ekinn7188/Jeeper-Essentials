package jeeper.essentials.commands;

public enum Permission {
    AFK("jeeper.afk"),
    BACK("jeeper.back"),
    BAN("jeeper.ban"),
    BAN_IP("jeeper.ipban"),
    BROADCAST("jeeper.broadcast"),
    BYPASSCHAT("jeeper.chat.bypass"),
    CHATCOLOR("jeeper.chat.color"),
    CLEARCHAT("jeeper.chat.clear"),
    CLEARLAG("jeeper.clearlag"),
    COOLDOWN("jeeper.cooldown"),
    DELETEWARP("jeeper.warps.delete"),
    
    ENCHANT("minecraft.command.enchant"),
    ESSENTIALS("jeeper.essentials"),
    GAMEMODE("minecraft.command.gamerule"),
    HAT("jeeper.hat"),
    HISTORY("jeeper.history"),
    KICK("jeeper.kick"),
    MEMORY("jeeper.memory"),
    MUTE("jeeper.mute"),
    MUTECHAT("jeeper.chat.mute"),
    NICKNAME("jeeper.nickname"),
    
    PLUGIN("jeeper.plugin"),
    POWERTOOL("jeeper.powertool"),
    SETHOME("jeeper.sethome"),
    SETSPAWN("jeeper.setspawn"),
    SETWARP("jeeper.warps.set"),
    SUDO("jeeper.sudo"),
    TPA("jeeper.tpa"),
    TPS("jeeper.tps"),
    VANILLA_TELEPORT("minecraft.command.tp"),
    VIEW_REPORT("jeeper.reports.view"),
    WARN("jeeper.warn");
    
    private final String name;
    
    Permission(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
