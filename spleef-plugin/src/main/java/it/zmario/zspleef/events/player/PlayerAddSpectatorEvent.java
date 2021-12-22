package it.zmario.zspleef.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAddSpectatorEvent extends Event {

    private final HandlerList handlers = new HandlerList();
    private final Player player;

    public PlayerAddSpectatorEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public Player getPlayer() {
        return player;
    }
}
