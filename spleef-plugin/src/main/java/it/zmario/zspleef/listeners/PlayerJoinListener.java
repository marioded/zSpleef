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
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Utils.isSetup()) {
            if (p.hasPermission("zspleef.setup"))
                p.performCommand("spleef");
            return;
        }
        switch (GameState.getState()) {
            case WAITING:
                p.getInventory().setArmorContents(null);
                p.getInventory().clear();
                Main.getInstance().getArena().addPlayer(p);
                if (Main.getInstance().getArena().getPlayers().size() >= Main.getInstance().getArena().getMinPlayers()) Main.getInstance().getArena().start(false);
                p.getInventory().setItem(ConfigHandler.getMessages().getInt("LeaveItem.Slot"), Utils.getLeaveItem());
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.WaitingLocation"))), 10);
                e.setJoinMessage(Messages.ARENA_JOIN_MESSAGE.getString(p).replace("%player%", p.getName()).replaceAll("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size()).replace("%max_players%", String.valueOf(Main.getInstance().getArena().getMaxPlayers()))));
                return;
            case INGAME:
                e.setJoinMessage(null);
                p.getInventory().setArmorContents(null);
                p.getInventory().clear();
                Main.getInstance().getArena().addSpectator(p);
                return;
        }

    }
}
