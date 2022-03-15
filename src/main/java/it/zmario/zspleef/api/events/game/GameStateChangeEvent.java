package it.zmario.zspleef.api.events.game;

import it.zmario.zspleef.enums.GameState;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GameStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final GameState newState;
    private final GameState oldState;

    public GameStateChangeEvent(GameState newState, GameState oldState) {
        this.newState = newState;
        this.oldState = oldState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
