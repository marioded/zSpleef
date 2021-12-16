package it.zmario.zspleef.enums;

import it.zmario.zspleef.utils.ConfigHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Messages {

    ARENA_JOIN_MESSAGE("JoinMessage"),
    ARENA_LEAVE_MESSAGE("LeaveMessage"),
    GAME_CANCELLED_WAITING_PLAYERS("Game.CancelledWaitingPlayers"),
    GAME_STARTING_MESSAGE("Game.StartingMessage"),
    GAME_PLAYER_ELIMINATED("Game.PlayerEliminated"),
    GAME_STARTED("Game.Started"),
    FORCESTART_STARTING("ForceStart.Starting"),
    FORCESTART_ALREADYSTARTED("ForceStart.AlreadyStarted"),
    FORCESTOP_STOPPING("ForceStop.Stopping"),
    FORCESTOP_ALREADYSTOPPED("ForceStop.AlreadyStopped");

    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public String getNoColorString(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return ConfigHandler.getMessages().getString(PlaceholderAPI.setPlaceholders(player, path));
        return ConfigHandler.getMessages().getString(path);
    }

    public String getString(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return ChatColor.translateAlternateColorCodes('&', ConfigHandler.getMessages().getString(PlaceholderAPI.setPlaceholders(player, path)));
        return ChatColor.translateAlternateColorCodes('&', ConfigHandler.getMessages().getString(path));
    }

    public List<String> getStringList(Player player) {
        List<String> stringList = new ArrayList<>();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            ConfigHandler.getMessages().getStringList(path).forEach(string -> stringList.add(PlaceholderAPI.setPlaceholders(player, string)));
        else
            stringList.addAll(ConfigHandler.getMessages().getStringList(path));
        return stringList;
    }

    public int getInt() {
        return ConfigHandler.getMessages().getInt(path);
    }

    public boolean getBoolean() {
        return ConfigHandler.getMessages().getBoolean(path);
    }
}
