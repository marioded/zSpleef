package it.zmario.zspleef.listeners;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!Utils.isSetup() && GameState.isState(GameState.INGAME) && Main.getInstance().getArena().isPlayer(p) && p.getLocation().getY() == ConfigHandler.getConfig().getInt("Arena.DeathLevel")) {
            Main.getInstance().getArena().addSpectator(p);
            for (Player online : Bukkit.getOnlinePlayers()) {
                Bukkit.broadcastMessage(Messages.GAME_PLAYER_ELIMINATED.getString(online).replaceAll("%player%", p.getName()));
            }
        }
    }
}
