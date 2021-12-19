package it.zmario.zspleef.arena;

import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.tasks.GameStartTask;
import it.zmario.zspleef.utils.Cuboid;
import it.zmario.zspleef.utils.Debug;
import it.zmario.zspleef.utils.Utils;
import it.zmario.zspleef.utils.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpleefArena {

    private final Set<Block> blocksBreaked;
    private final List<UUID> playersInGame;
    private final List<UUID> spectators;
    private final Cuboid cuboid;
    private GameStartTask startTask;

    private boolean isStopping;
    private boolean isStarting;

    public SpleefArena(Main main, Location pos1, Location pos2) {
        GameState.setGameState(GameState.WAITING);
        blocksBreaked = new HashSet<>();
        playersInGame  = new ArrayList<>();
        spectators = new ArrayList<>();
        cuboid = new Cuboid(pos1, pos2);
        isStopping = false;
    }

    public List<UUID> getPlayers() {
        return playersInGame;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public void addPlayer(Player p) {
        playersInGame.add(p.getUniqueId());
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(p.getMaxHealth());
        p.setSaturation(20);
        p.setExp(1);
        p.setLevel(0);
        p.getActivePotionEffects().forEach(effect -> p.removePotionEffect(effect.getType()));
    }

    public void removePlayer(Player p) {
        playersInGame.remove(p.getUniqueId());
    }

    public void addSpectator(Player p) {
        if (getPlayers().contains(p.getUniqueId())) removePlayer(p);
        for (UUID gamePlayers : playersInGame) {
            Bukkit.getPlayer(gamePlayers).hidePlayer(p);
        }
        p.setAllowFlight(true);
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();
        p.setHealth(p.getMaxHealth());
        p.setSaturation(20);
        p.setExp(1);
        p.setLevel(0);
        p.addPotionEffect(PotionEffectType.NIGHT_VISION.createEffect(Integer.MAX_VALUE, 1));
        p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.SpectatorLocation")));
        if (ConfigHandler.getMessages().getBoolean("LeaveItem.Enabled")) p.getInventory().setItem(ConfigHandler.getMessages().getInt("LeaveItem.Slot"), Utils.getLeaveItem());
        spectators.add(p.getUniqueId());
    }

    public void removeSpectator(Player p) {
        spectators.remove(p.getUniqueId());
    }

    public boolean isPlayer(Player p) {
        return playersInGame.contains(p.getUniqueId());
    }

    public Set<Block> getBlocksBreaked() {
        return blocksBreaked;
    }

    public void addBlockToBreaked(Block block) {
        blocksBreaked.add(block);
    }

    public void start(boolean forceStart, boolean debug) {
        startTask = new GameStartTask(forceStart, debug);
        startTask.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public void restart() {
        Debug.warn("&eShutting down because of game end...");
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.kickPlayer(Messages.GAME_KICK_FINISHED.getString(online));
            }
            Bukkit.shutdown();
            Bukkit.getScheduler().cancelAllTasks();
        }, ConfigHandler.getConfig().getInt("Settings.SecondsBeforeRestart") * 20L);

    }

    public void checkWin() {
        if (isStopping) return;
        if (Main.getInstance().getArena().getPlayers().size() == 1 && GameState.isState(GameState.INGAME) && Main.getInstance().getArena().getMinPlayers() > 1) {
            Player p = Bukkit.getPlayer(Main.getInstance().getArena().getPlayers().get(0));
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online != p) Utils.sendTitle(online, "Titles.Game.Lose", 0, 60, 0);
                online.sendMessage(Messages.GAME_FINISHED_MESSAGE.getString(online).replaceAll("%winner%", p.getName()));
            }
            Utils.sendTitle(p, "Titles.Game.Victory", 0, 60, 0);
            Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> Utils.spawnFireworks(p.getLocation(), 2), 10L, 15L);
            p.getInventory().clear();
            p.setAllowFlight(true);
            setStopping(true);
            restart();
        } else if (Main.getInstance().getArena().getPlayers().size() == 0) {
            setStopping(true);
            restart();
        }
    }

    public int getMinPlayers() {
        return ConfigHandler.getConfig().getInt("Arena.MinPlayers");
    }

    public int getMaxPlayers() {
        return ConfigHandler.getConfig().getInt("Arena.MaxPlayers");
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public boolean isStopping() {
        return isStopping;
    }

    public void setStopping(boolean value) {
        isStopping = value;
    }

    public boolean isStarting() {
        return isStarting;
    }

    public void setStarting(boolean value) {
        isStarting = value;
    }

    public GameStartTask getStartTask() {
        return startTask;
    }
}
