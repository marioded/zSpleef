package it.zmario.zspleef.utils;

import org.bukkit.Bukkit;

public class Debug {

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(Utils.colorize("&e&lzSpleef &7(&bInfo&7) &8» &7" + message));
    }

    public static void warn(String message) {
        Bukkit.getConsoleSender().sendMessage(Utils.colorize("&e&lzSpleef &7(&eWarn&7) &8» &c" + message));
    }

    public static void severe(String message) {
        Bukkit.getConsoleSender().sendMessage(Utils.colorize("&e&lzSpleef &7(&cError&7) &8» &c" + message));
    }

    public static boolean isDebugEnabled() {
        return ConfigHandler.getConfig().getBoolean("Debug");
    }
}
