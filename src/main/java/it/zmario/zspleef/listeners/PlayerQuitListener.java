package it.zmario.zspleef.listeners;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.api.events.player.PlayerEliminateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        zSpleef.getInstance().getArena().removeBoard(p);
        switch (GameState.getState()) {
            case WAITING:
                if (zSpleef.getInstance().getArena().isPlayer(p)) {
                    e.setQuitMessage(Messages.ARENA_LEAVE_MESSAGE.getString(p).replace("%player%", p.getName()).replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size() - 1)).replace("%max_players%", String.valueOf(zSpleef.getInstance().getArena().getMaxPlayers())));
                    zSpleef.getInstance().getArena().removePlayer(p);
                } else {
                    zSpleef.getInstance().getArena().removeSpectator(p);
                    e.setQuitMessage(null);
                }
                return;
            case INGAME:
                e.setQuitMessage(null);
                zSpleef.getInstance().getArena().removeSpectator(p);
                if (zSpleef.getInstance().getArena().isPlayer(p) && !zSpleef.getInstance().getArena().isStopping()) {
                    PlayerEliminateEvent playerEliminateEvent = new PlayerEliminateEvent(p);
                    Bukkit.getPluginManager().callEvent(playerEliminateEvent);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Messages.GAME_PLAYER_ELIMINATED_QUIT.getString(p).replace("%player%", p.getName()).replace("%players%", String.valueOf(zSpleef.getInstance().getArena().getPlayers().size())));
                    }
                    zSpleef.getInstance().getArena().removePlayer(p);
                    zSpleef.getInstance().getArena().checkWin();
                }

        }
    }
}
