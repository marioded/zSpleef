package it.zmario.zspleef.events.game;

import it.zmario.zspleef.enums.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event {

    private final HandlerList handlers = new HandlerList();

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

    public GameState getNewState() {
        return newState;
    }

    public GameState getOldState() {
        return oldState;
    }
}
