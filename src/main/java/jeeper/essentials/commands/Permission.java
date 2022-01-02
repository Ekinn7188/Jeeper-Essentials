package jeeper.essentials.commands;

public enum Permission {
    NICKNAME("jeeper.nickname"),
    ESSENTIALS("jeeper.essentials"),
    BROADCAST("jeeper.broadcast"),
    SETWARP("jeeper.warps.set"),
    SETSPAWN("jeeper.setspawn"),
    SETHOME("jeeper.sethome"),
    DELETEWARP("jeeper.warps.delete"),
    CLEARCHAT("jeeper.chat.clear"),
    MUTECHAT("jeeper.chat.mute"),
    BYPASSCHAT("jeeper.chat.bypass"),
    CHATCOLOR("jeeper.chat.color"),
    AFK("jeeper.afk"),
    COOLDOWN("jeeper.cooldown"),
    SUDO("jeeper.sudo"),
    BAN("jeeper.ban"),
    KICK("jeeper.kick"),
    MUTE("jeeper.mute"),
    WARN("jeeper.warn"),
    BAN_IP("jeeper.ipban"),
    MEMORY("jeeper.memory"),
    BACK("jeeper.back"),
    HISTORY("jeeper.history");

    private final String name;

    Permission(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
