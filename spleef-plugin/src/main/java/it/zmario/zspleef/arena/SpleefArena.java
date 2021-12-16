package it.zmario.zspleef.arena;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.tasks.GameEndTask;
import it.zmario.zspleef.tasks.GameStartTask;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Cuboid;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class SpleefArena {

    private final Set<Block> blocksBreaked;
    private final List<UUID> playersInGame;
    private final List<UUID> spectators;
    private final Cuboid cuboid;

    private boolean isStopping;

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
    }

    public void removePlayer(Player p) {
        playersInGame.remove(p.getUniqueId());
    }

    public void addSpectator(Player p) {
        if (getPlayers().contains(p.getUniqueId())) removePlayer(p);
        for (UUID gamePlayers : playersInGame) {
            Bukkit.getPlayer(gamePlayers).hidePlayer(p);
        }
        p.getInventory().addItem(Utils.getLeaveItem());
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

    public void start(boolean forceStart) {
        new GameStartTask(forceStart).runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public void restart() {
        setStopping(true);
        new GameEndTask().runTaskAsynchronously(Main.getInstance());
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
}
