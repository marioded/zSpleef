package it.zmario.zspleef.api.events.game;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class GameEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player winner;

    public GameEndEvent(Player winner) {
        this.winner = winner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
