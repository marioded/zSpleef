package it.zmario.zspleef.events.game;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    private final HandlerList handlers = new HandlerList();
    private final Player winner;

    public GameEndEvent(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
