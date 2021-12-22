package it.zmario.zspleef.events.game;

import it.zmario.zspleef.arena.Powerup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GamePowerupSpawnEvent extends Event {

    private final HandlerList handlers = new HandlerList();

    private final Powerup powerup;
    private boolean cancelled = false;

    public GamePowerupSpawnEvent( Powerup powerup) {
        this.powerup = powerup;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    public Powerup getPowerup() {
        return powerup;
    }
}
