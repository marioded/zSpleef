package it.zmario.zspleef.tasks;

import it.zmario.zspleef.scoreboard.SpleefBoard;
import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.api.events.game.GameStateChangeEvent;
import it.zmario.zspleef.utils.Utils;
import it.zmario.zspleef.utils.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameStartTask extends BukkitRunnable {

    boolean forceStart;
    boolean isDebug;
    public int seconds;
    private final zSpleef plugin;

    public GameStartTask(zSpleef plugin, boolean forceStart, boolean isDebug) {
        this.plugin = plugin;
        this.forceStart = forceStart;
        this.isDebug = isDebug;
        this.seconds = forceStart ? 5 : ConfigHandler.getConfig().getInt("Settings.SecondsBeforeStart");
    }

    public void run() {
        List<String> lines = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        if (plugin.getArena().getPlayers().size() < plugin.getArena().getMinPlayers() && !forceStart) {
            GameState.setGameState(GameState.WAITING);
            plugin.getArena().setStarting(false);
            for (Player online : Bukkit.getOnlinePlayers()) {
                SpleefBoard board = plugin.getArena().getBoards().get(online.getUniqueId());
                Messages.SCOREBOARD_WAITING_LINES.getStringList(online).forEach(line -> lines.add(Utils.colorize(line.replace("%player%", online.getName()).replace("%date%", dateFormat.format(date)).replace("%players%", String.valueOf(plugin.getArena().getPlayers().size())).replace("%max_players%", String.valueOf(plugin.getArena().getMaxPlayers())))));
                board.updateTitle(Messages.SCOREBOARD_WAITING_TITLE.getString(online));
                board.updateLines(lines);
                Utils.sendTitle(online, ConfigHandler.getMessages().getString("Titles.Game.NotEnoughPlayers"), 0, 40, 20);
                Utils.playSound(online, "NotEnoughPlayersSound");
                online.sendMessage(Messages.GAME_CANCELLED_WAITING_PLAYERS.getString(online));
            }
            cancel();
            return;
        }
        plugin.getArena().setStarting(true);

        for (Player online : Bukkit.getOnlinePlayers()) {
            lines.clear();
            SpleefBoard board = plugin.getArena().getBoards().get(online.getUniqueId());
            Messages.SCOREBOARD_STARTING_LINES.getStringList(online).forEach(line -> lines.add(Utils.colorize(line.replace("%seconds%", String.valueOf(plugin.getArena().getStartTask().seconds)).replace("%player%", online.getName()).replace("%date%", dateFormat.format(date)).replace("%players%", String.valueOf(plugin.getArena().getPlayers().size())).replace("%max_players%", String.valueOf(plugin.getArena().getMaxPlayers())))));
            board.updateTitle(Messages.SCOREBOARD_STARTING_TITLE.getString(online));
            board.updateLines(lines);
        }
        if (seconds == 0) {
            teleportPlayers();
            plugin.getArena().checkWin();
            GameStateChangeEvent gameStateChangeEvent = new GameStateChangeEvent(GameState.INGAME, GameState.getState());
            Bukkit.getPluginManager().callEvent(gameStateChangeEvent);
            GameState.setGameState(GameState.INGAME);
            for (Player online : Bukkit.getOnlinePlayers()) {
                lines.clear();
                SpleefBoard board = plugin.getArena().getBoards().get(online.getUniqueId());
                Messages.SCOREBOARD_PLAYING_LINES.getStringList(online).forEach(line -> lines.add(Utils.colorize(line.replace("%player%", online.getName()).replace("%date%", dateFormat.format(date)).replace("%players%", String.valueOf(zSpleef.getInstance().getArena().getPlayers().size())).replace("%time_left%", zSpleef.getInstance().getArena().getTimeLeft()))));
                board.updateTitle(Messages.SCOREBOARD_PLAYING_TITLE.getString(online));
                board.updateLines(lines);
            }
            plugin.getArena().setStarting(false);
            plugin.getArena().startTasks();
            cancel();
            return;
        }
        if (seconds % 10 == 0 || seconds <= 5)
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Messages.GAME_STARTING_MESSAGE.getString(online).replace("%seconds%", String.valueOf(seconds)));
            }
        if (seconds <= 5 && ConfigHandler.getConfig().getBoolean("Settings.TitlesEnabled")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                Utils.playSound(online, "CountdownSound");
                Utils.sendTitle(online, ConfigHandler.getMessages().getString("Titles.Game.Countdown").replace("%seconds%", String.valueOf(seconds)), 0, 20, 20);
            }
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setLevel(seconds);
        }
        seconds = seconds - 1;
    }

    public void teleportPlayers() {
        List<Location> locations = new ArrayList<>();
        for (String spawnpoint : ConfigHandler.getConfig().getConfigurationSection("Arena.Spawns").getKeys(false)){
            Location loc = Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.Spawns." + spawnpoint));
            locations.add(loc);
        }

        Random rand = new Random();
        for (Player online : Bukkit.getOnlinePlayers()) {
            Utils.playSound(online, "StartSound");
            Utils.sendTitle(online, ConfigHandler.getMessages().getString("Titles.Game.Start"), 0, 40, 20);
            online.sendMessage(Messages.GAME_STARTED.getString(online));
            online.setExp(1);
            online.setLevel(0);
        }
        for (UUID onlineUUID : plugin.getArena().getPlayers()) {
            Player online = Bukkit.getPlayer(onlineUUID);
            online.getInventory().clear();
            online.getInventory().setArmorContents(null);
            if (ConfigHandler.getMessages().getBoolean("ShovelItem.Enabled")) online.getInventory().addItem(Utils.getShovelItem());
            online.teleport(locations.remove(rand.nextInt(locations.size())));
            plugin.getArena().checkWin();
        }
    }
}
