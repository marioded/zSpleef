package it.zmario.zspleef;

import it.zmario.zspleef.arena.Powerup;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class zSpleefAPI {

    public List<UUID> getPlayers() {
        return Main.getInstance().getArena().getPlayers();
    }

    public List<UUID> getSpectators() {
        return Main.getInstance().getArena().getSpectators();
    }

    public List<Powerup> getActivePowerups() {
        return Main.getInstance().getArena().getActivePowerups();
    }

    public boolean isPlayer(Player player) {
        return Main.getInstance().getArena().getPlayers().contains(player.getUniqueId());
    }

    public void restartGame() {
        Main.getInstance().getArena().restart();
    }
}
