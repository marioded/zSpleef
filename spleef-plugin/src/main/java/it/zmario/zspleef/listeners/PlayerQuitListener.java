package it.zmario.zspleef.listeners;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Utils.isSetup()) return;
        switch (GameState.getState()) {
            case WAITING:
                e.setQuitMessage(Messages.ARENA_LEAVE_MESSAGE.getString(p).replace("%player%", p.getName()).replaceAll("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()).replace("%max_players%", String.valueOf(Main.getInstance().getArena().getMaxPlayers()))));
                Main.getInstance().getArena().removePlayer(p);
                return;
            case INGAME:
                e.setQuitMessage(null);
                Main.getInstance().getArena().removePlayer(p);
                Main.getInstance().getArena().removeSpectator(p);
        }
    }
}
