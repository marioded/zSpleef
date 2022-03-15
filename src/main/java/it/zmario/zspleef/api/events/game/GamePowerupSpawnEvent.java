package it.zmario.zspleef.api.events.game;

import it.zmario.zspleef.arena.Powerup;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GamePowerupSpawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Powerup powerup;
    @Setter private boolean cancelled;

    public GamePowerupSpawnEvent( Powerup powerup) {
        this.powerup = powerup;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
