package it.zmario.zspleef.enums;

import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public enum Messages {

    ARENA_JOIN_MESSAGE("JoinMessage"),
    ARENA_LEAVE_MESSAGE("LeaveMessage"),
    ERROR_REACHEDMAXPLAYERS("Errors.ReachedMaxPlayers"),
    ERROR_BORDER("Errors.BorderError"),
    ERROR_NOPERMISSION("Errors.NoPermission"),
    ERROR_INGAMENOWSPECTATOR("Errors.IngameNowSpectator"),
    GAME_CANCELLED_WAITING_PLAYERS("Game.CancelledWaitingPlayers"),
    GAME_MINIMUM_PLAYERS_REACHED("Game.MinimumPlayersReached"),
    GAME_STARTING_MESSAGE("Game.Starting"),
    GAME_FINISHED_MESSAGE("Game.Finish"),
    GAME_PLAYER_ELIMINATED("Game.PlayerEliminated"),
    GAME_PLAYER_ELIMINATED_QUIT("Game.PlayerEliminatedQuit"),
    GAME_STARTED("Game.Started"),
    GAME_NOWSPECTATOR("Game.NowSpectator"),
    GAME_TIME_FINISHED("Game.TimeFinished"),
    GAME_KICK_FINISHED("Game.KickFinished"),
    FORCESTART_STARTING("ForceStart.Starting"),
    FORCESTART_ALREADYSTARTED("ForceStart.AlreadyStarted"),
    FORCESTOP_STOPPING("ForceStop.Stopping"),
    FORCESTOP_ALREADYSTOPPED("ForceStop.AlreadyStopped"),
    FORCESTOP_NOTSTARTED("ForceStop.NotStarted"),
    SCOREBOARD_WAITING_TITLE("Scoreboard.Waiting.Title"),
    SCOREBOARD_WAITING_LINES("Scoreboard.Waiting.Lines"),
    SCOREBOARD_STARTING_TITLE("Scoreboard.Starting.Title"),
    SCOREBOARD_STARTING_LINES("Scoreboard.Starting.Lines"),
    SCOREBOARD_PLAYING_TITLE("Scoreboard.Playing.Title"),
    SCOREBOARD_PLAYING_LINES("Scoreboard.Playing.Lines"),
    SCOREBOARD_ENDING_TITLE("Scoreboard.Ending.Title"),
    SCOREBOARD_ENDING_LINES("Scoreboard.Ending.Lines"),
    CHATFORMAT_PLAYER("ChatFormat.Player"),
    CHATFORMAT_SPECTATOR("ChatFormat.Spectator");

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
            ConfigHandler.getMessages().getStringList(path).forEach(string -> stringList.add(PlaceholderAPI.setPlaceholders(player, Utils.colorize(string))));
        else
            ConfigHandler.getMessages().getStringList(path).forEach(string -> stringList.add(Utils.colorize(string)));
        return stringList;
    }

    public int getInt() {
        return ConfigHandler.getMessages().getInt(path);
    }

    public boolean getBoolean() {
        return ConfigHandler.getMessages().getBoolean(path);
    }
}
