package it.zmario.zspleef.api.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLevelupEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int level;
    private final int xp;

    public PlayerLevelupEvent(Player player, int level, int xp) {
        this.player = player;
        this.level = level;
        this.xp = xp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevel() {
        return level;
    }

    public int getXP() {
        return xp;
    }
}
