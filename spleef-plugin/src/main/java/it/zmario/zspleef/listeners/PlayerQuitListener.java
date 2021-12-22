package it.zmario.zspleef.listeners;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.events.player.PlayerEliminateEvent;
import it.zmario.zspleef.scoreboard.SpleefBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        SpleefBoard board = Main.getInstance().getArena().getBoards().remove(p.getUniqueId());

        if (board != null) {
            board.delete();
        }

        switch (GameState.getState()) {
            case WAITING:
                e.setQuitMessage(Messages.ARENA_LEAVE_MESSAGE.getString(p).replace("%player%", p.getName()).replaceAll("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size() - 1)).replaceAll("%max_players%", String.valueOf(Main.getInstance().getArena().getMaxPlayers())));
                Main.getInstance().getArena().removePlayer(p);
                return;
            case INGAME:
                e.setQuitMessage(null);
                if (Main.getInstance().getArena().isPlayer(p) && !Main.getInstance().getArena().isStopping()) {
                    PlayerEliminateEvent playerEliminateEvent = new PlayerEliminateEvent(p);
                    Bukkit.getPluginManager().callEvent(playerEliminateEvent);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Messages.GAME_PLAYER_ELIMINATED_QUIT.getString(p).replaceAll("%player%", p.getName()).replaceAll("%players%", String.valueOf(Main.getInstance().getArena().getPlayers().size())));
                    }
                    Main.getInstance().getArena().checkWin();
                }
                Main.getInstance().getArena().removePlayer(p);
                Main.getInstance().getArena().removeSpectator(p);
        }
    }
}
