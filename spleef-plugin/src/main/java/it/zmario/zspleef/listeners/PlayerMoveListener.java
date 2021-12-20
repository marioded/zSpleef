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
        if (GameState.isState(GameState.INGAME) && Main.getInstance().getArena().isPlayer(p) && p.getLocation().getY() <= ConfigHandler.getConfig().getInt("Arena.DeathLevel") && !Main.getInstance().getArena().isStopping()) {
            p.getWorld().strikeLightningEffect(p.getLocation());
            Utils.sendTitle(p, "Titles.Game.Eliminated", 0, 60, 0);
            Utils.playSound(p, "EliminatedSound");
            Main.getInstance().getArena().addSpectator(p);
            p.sendMessage(Messages.GAME_NOWSPECTATOR.getString(p));
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.sendMessage(Messages.GAME_PLAYER_ELIMINATED.getString(online).replaceAll("%player%", p.getName()));
            }
            Main.getInstance().getArena().checkWin();
            return;
        }
        if (p.getLocation().getY() <= ConfigHandler.getConfig().getInt("Arena.DeathLevel"))
            p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.SpectatorLocation")));
    }
}
