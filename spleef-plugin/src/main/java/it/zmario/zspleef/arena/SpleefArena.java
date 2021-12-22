package it.zmario.zspleef.arena;

import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.events.game.GameEndEvent;
import it.zmario.zspleef.events.game.GameStateChangeEvent;
import it.zmario.zspleef.events.player.PlayerAddSpectatorEvent;
import it.zmario.zspleef.scoreboard.SpleefBoard;
import it.zmario.zspleef.tasks.GameStartTask;
import it.zmario.zspleef.tasks.PowerupsTask;
import it.zmario.zspleef.tasks.TimeLeftTask;
import it.zmario.zspleef.utils.Cuboid;
import it.zmario.zspleef.utils.Debug;
import it.zmario.zspleef.utils.Utils;
import it.zmario.zspleef.utils.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpleefArena {
    
    private final Main main;
    
    private final Set<Block> blocksBreaked;
    private final List<Powerup> powerups;
    private final List<Powerup> activePowerups;
    private final List<UUID> playersInGame;
    private final List<UUID> spectators;
    private final Map<UUID, SpleefBoard> boards;
    private final Cuboid cuboid;
    private TimeLeftTask timeLeftTask;
    private GameStartTask startTask;
    private PowerupsTask powerupsTask;
    private UUID winnerUUID;

    private boolean isStopping;
    private boolean isStarting;

    public SpleefArena(Main main, Location pos1, Location pos2) {
        GameState.setGameState(GameState.WAITING);
        this.main = main;
        blocksBreaked = new HashSet<>();
        powerups = new ArrayList<>();
        activePowerups = new ArrayList<>();
        playersInGame  = new ArrayList<>();
        spectators = new ArrayList<>();
        cuboid = new Cuboid(pos1, pos2);
        boards = new HashMap<>();
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
        PlayerAddSpectatorEvent playerAddSpectatorEvent = new PlayerAddSpectatorEvent(p);
        Bukkit.getPluginManager().callEvent(playerAddSpectatorEvent);
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

    public List<Powerup> getPowerups() {
        return powerups;
    }

    public List<Powerup> getActivePowerups() {
        return activePowerups;
    }

    public void start(boolean forceStart, boolean debug) {
        startTask = new GameStartTask(main, forceStart, debug);
        startTask.runTaskTimer(main, 0L, 20L);
    }

    public void restart() {
        setStopping(true);
        getTimeLeftTask().cancel();
        if (getPowerupsTask() != null) getPowerupsTask().cancel();
        Debug.warn("&eShutting down because of game end...");
        Bukkit.getScheduler().runTaskLater(main, () -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.kickPlayer(Messages.GAME_KICK_FINISHED.getString(online));
            }
            Bukkit.shutdown();
            Bukkit.getScheduler().cancelAllTasks();
        }, ConfigHandler.getConfig().getInt("Settings.SecondsBeforeRestart") * 20L);

    }
    
    public void startTasks() {
        timeLeftTask = new TimeLeftTask();
        timeLeftTask.runTaskTimer(main, 0L, 20L);
        if (ConfigHandler.getConfig().getBoolean("Settings.Powerups.Enabled") && Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            powerupsTask = new PowerupsTask();
            long seconds = ConfigHandler.getConfig().getInt("Settings.Powerups.SecondsBetweenSpawns") * 20L;
            powerupsTask.runTaskTimer(main, seconds, seconds);
        }
    }

    public void checkWin() {
        if (isStopping) return;
        if (main.getArena().getPlayers().size() == 1 && GameState.isState(GameState.INGAME) && main.getArena().getMinPlayers() > 1) {
            Player p = Bukkit.getPlayer(main.getArena().getPlayers().get(0));
            GameEndEvent endEvent = new GameEndEvent(p);
            Bukkit.getPluginManager().callEvent(endEvent);
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online != p) Utils.sendTitle(online, "Titles.Game.Lose", 0, 60, 0);
                online.sendMessage(Messages.GAME_FINISHED_MESSAGE.getString(online).replaceAll("%winner%", p.getName()));
            }
            winnerUUID = p.getUniqueId();
            Utils.sendTitle(p, "Titles.Game.Victory", 0, 60, 0);
            Bukkit.getScheduler().runTaskTimer(main, () -> Utils.spawnFireworks(p.getLocation(), 2), 10L, 15L);
            p.getInventory().clear();
            p.setAllowFlight(true);
            restart();
        } else if (main.getArena().getPlayers().size() == 0) {
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

    public TimeLeftTask getTimeLeftTask() {
        return timeLeftTask;
    }

    public PowerupsTask getPowerupsTask() {
        return powerupsTask;
    }

    public Map<UUID, SpleefBoard> getBoards() {
        return boards;
    }

    public void addBoard(Player player, SpleefBoard board) {
        boards.put(player.getUniqueId(), board);
    }

    public void removeBoard(Player player) {
        getBoards().remove(player.getUniqueId());
    }

    public String getWinner() {
        return winnerUUID != null ? Bukkit.getPlayer(winnerUUID).getName() : "&c???";
    }

    public String getTimeLeft() {
        return getTimeLeftTask() != null ? String.valueOf(timeLeftTask.timeLeft) : "0";
    }
}
