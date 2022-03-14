package it.zmario.zspleef.api;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.arena.Powerup;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Expression;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SpleefAPI {

    /**
     * Gets all players in game
     * @return All players in game
     */

    public Collection<Player> getPlayers() {
        return zSpleef.getInstance().getArena().getPlayersList();
    }

    /**
     * Gets all spectators in game
     * @return All spectators in game
     */

    public Collection<Player> getSpectators() {
        return zSpleef.getInstance().getArena().getSpectatorsList();
    }

    /**
     * Gets all activated powerups
     * @return All active powerups
     */

    public Collection<Powerup> getActivePowerups() {
        return zSpleef.getInstance().getArena().getActivePowerups();
    }

    /**
     * Gets all loaded powerups
     * @return All loaded powerups
     */

    public Collection<Powerup> getLoadedPowerups() {
        return zSpleef.getInstance().getArena().getPowerups();
    }

    /**
     * Check if a player is playing
     * @return true if the player is playing
     */

    public boolean isPlayer(Player player) {
        return zSpleef.getInstance().getArena().getPlayers().contains(player.getUniqueId());
    }

    /**
     * Restarts the game
     */

    public void restartGame() {
        zSpleef.getInstance().getArena().restart();
    }

    /**
     * Get current XP of a player
     * @return xp of the player
     */
    public int getXP(Player player) {
        return zSpleef.getInstance().getLevels().getXP(player);
    }

    /**
     * Get current level of a player
     * @return level of the player
     */
    public int getLevel(Player player) {
        return zSpleef.getInstance().getLevels().getLevel(player);
    }

    /**
     * Get remaining XP to levelup of a player
     * @return xp needed to level up
     */
    private double getNextXP(Player player) {
        return Expression.eval(ConfigHandler.getConfig().getString("Settings.Levels.LevelupExpression").replace("level", String.valueOf(getLevel(player))).replace("xp", String.valueOf(getXP(player))));
    }
}
