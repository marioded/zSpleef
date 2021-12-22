package it.zmario.zspleef.events.player;

import it.zmario.zspleef.arena.Powerup;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerPowerupPickupEvent extends Event {

    private final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Powerup powerup;
    private boolean cancelled = false;

    public PlayerPowerupPickupEvent(Player player, Powerup powerup) {
        this.player = player;
        this.powerup = powerup;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public Powerup getPowerup() {
        return powerup;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
