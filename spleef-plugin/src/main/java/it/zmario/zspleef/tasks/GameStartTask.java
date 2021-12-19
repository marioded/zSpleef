package it.zmario.zspleef.tasks;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.Utils;
import it.zmario.zspleef.utils.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GameStartTask extends BukkitRunnable {

    boolean forceStart;
    boolean isDebug;
    int seconds;

    public GameStartTask(boolean forceStart, boolean isDebug) {
        this.forceStart = forceStart;
        this.isDebug = isDebug;
        this.seconds = forceStart ? 5 : ConfigHandler.getConfig().getInt("Settings.SecondsBeforeStart");
    }

    public void run() {
        if (Main.getInstance().getArena().getPlayers().size() < Main.getInstance().getArena().getMinPlayers() && !forceStart) {
            GameState.setGameState(GameState.WAITING);
            for (Player online : Bukkit.getOnlinePlayers()) {
                Utils.sendTitle(online, "Titles.Game.NotEnoughPlayers", 0, 40, 20);
                Utils.playSound(online, "NotEnoughPlayersSound");
                online.sendMessage(Messages.GAME_CANCELLED_WAITING_PLAYERS.getString(online));
            }
            cancel();
            return;
        }
        Main.getInstance().getArena().setStarting(true);
        if (seconds == 0) {
            teleportPlayers();
            Main.getInstance().getArena().checkWin();
            GameState.setGameState(GameState.INGAME);
            cancel();
            return;
        }
        if (seconds % 10 == 0 || seconds <= 5)
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Messages.GAME_STARTING_MESSAGE.getString(online).replace("%seconds%", String.valueOf(seconds)));
            }
        if (seconds <= 5 && ConfigHandler.getConfig().getBoolean("Settings.TitlesEnabled")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                String title = "Titles.Game.Countdown".replace("%seconds%", String.valueOf(seconds));
                Utils.playSound(online, "CountdownSound");
                Utils.sendTitle(online, title, 0, 20, 20);
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
            Utils.sendTitle(online, "Titles.Game.Start", 0, 40, 20);
            online.sendMessage(Messages.GAME_STARTED.getString(online));
            online.setExp(1);
            online.setLevel(0);
        }
        for (UUID onlineUUID : Main.getInstance().getArena().getPlayers()) {
            Player online = Bukkit.getPlayer(onlineUUID);
            online.getInventory().clear();
            online.getInventory().setArmorContents(null);
            if (ConfigHandler.getMessages().getBoolean("ShovelItem.Enabled")) online.getInventory().addItem(Utils.getShovelItem());
            online.teleport(locations.remove(rand.nextInt(locations.size())));
            Main.getInstance().getArena().checkWin();
        }
    }
}
