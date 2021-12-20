package it.zmario.zspleef.listeners;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.scoreboard.SpleefBoard;
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
        SpleefBoard board = new SpleefBoard(p);
        board.updateTitle(Messages.SCOREBOARD_WAITING_TITLE.getString(p));
        Main.getInstance().getArena().addBoard(p, board);
        switch (GameState.getState()) {
            case WAITING:
                if (Bukkit.getOnlinePlayers().size() > Main.getInstance().getArena().getMaxPlayers()) {
                    e.setJoinMessage(null);
                    p.sendMessage(Messages.ERROR_REACHEDMAXPLAYERS.getString(p));
                    Main.getInstance().getArena().addSpectator(p);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.SpectatorLocation"))), 5);
                    return;
                }
                Main.getInstance().getArena().addPlayer(p);
                if (Main.getInstance().getArena().getPlayers().size() >= Main.getInstance().getArena().getMinPlayers() && !Main.getInstance().getArena().isStarting()) {
                    Main.getInstance().getArena().start(false, false);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Messages.GAME_MINIMUM_PLAYERS_REACHED.getString(online));
                    }
                }
                if (ConfigHandler.getMessages().getBoolean("LeaveItem.Enabled")) p.getInventory().setItem(ConfigHandler.getMessages().getInt("LeaveItem.Slot"), Utils.getLeaveItem());
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.WaitingLocation"))), 5);
                e.setJoinMessage(Messages.ARENA_JOIN_MESSAGE.getString(p).replace("%player%", p.getName()).replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size())).replaceAll("%max_players%", String.valueOf(Main.getInstance().getArena().getMaxPlayers())));
                return;
            case INGAME:
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.SpectatorLocation"))), 5);
                e.setJoinMessage(null);
                p.sendMessage(Messages.ERROR_INGAMENOWSPECTATOR.getString(p));
                Main.getInstance().getArena().addSpectator(p);
        }

    }
}