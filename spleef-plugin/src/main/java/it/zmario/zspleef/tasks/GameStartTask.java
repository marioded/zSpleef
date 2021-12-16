package it.zmario.zspleef.tasks;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.nms.NMS;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameStartTask extends BukkitRunnable {

    boolean forceStart;

    public GameStartTask(boolean forceStart) {
        this.forceStart = forceStart;
    }
    int seconds = ConfigHandler.getConfig().getInt("SecondsBeforeGameStart");

    public void run() {
        if (seconds == 0) {
            teleportPlayers();
            GameState.setGameState(GameState.INGAME);
            cancel();
            return;
        }
        if (Main.getInstance().getArena().getPlayers().size() < Main.getInstance().getArena().getMinPlayers() && !forceStart) {
            GameState.setGameState(GameState.WAITING);
            for (Player online : Bukkit.getOnlinePlayers())
                Bukkit.broadcastMessage(Messages.GAME_CANCELLED_WAITING_PLAYERS.getString(online));
            cancel();
            return;
        }
        if (seconds % 10 == 0 || seconds <= 5)
            for (Player online : Bukkit.getOnlinePlayers())
                Bukkit.broadcastMessage(Messages.GAME_STARTING_MESSAGE.getString(online).replaceAll("%seconds%", String.valueOf(seconds)));
        if (seconds <= 5 && ConfigHandler.getConfig().getBoolean("Settings.Titles.GameStart.Enabled")) {
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
        for (UUID onlineUUID : Main.getInstance().getArena().getPlayers()) {
            Player online = Bukkit.getPlayer(onlineUUID);
            online.sendMessage(Messages.GAME_STARTED.getString(online));
            online.getInventory().clear();
            online.teleport(locations.remove(rand.nextInt(locations.size())));
        }
    }
}
