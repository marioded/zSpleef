package it.zmario.zspleef.listeners;

import it.zmario.zspleef.scoreboard.SpleefBoard;
import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Utils.isSetup()) {
            if (p.hasPermission("zspleef.admin"))
                p.performCommand("spleef");
            return;
        }
        SpleefBoard board = new SpleefBoard(p);
        zSpleef.getInstance().getArena().addBoard(board);
        try {
            if (!zSpleef.getInstance().getSql().isPresent(p).get())
                zSpleef.getInstance().getSql().createPlayer(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> zSpleef.getInstance().getLevels().checkLevelup(p), 10L);
        List<String> lines = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        if (zSpleef.getInstance().getArena().isStarting()) {
            Messages.SCOREBOARD_STARTING_LINES.getStringList(p).forEach(line -> lines.add(Utils.colorize(line.replace("%seconds%", String.valueOf(zSpleef.getInstance().getArena().getStartTask().seconds)).replace("%player%", p.getName()).replace("%date%", dateFormat.format(date)).replace("%players%", String.valueOf(zSpleef.getInstance().getArena().getPlayers().size())).replace("%max_players%", String.valueOf(zSpleef.getInstance().getArena().getMaxPlayers())))));
            board.updateTitle(Messages.SCOREBOARD_STARTING_TITLE.getString(p));
            board.updateLines(lines);
            return;
        }
        if (zSpleef.getInstance().getArena().isStopping()) {
            Messages.SCOREBOARD_ENDING_LINES.getStringList(p).forEach(line -> lines.add(Utils.colorize(line.replace("%player%", p.getName()).replace("%date%", dateFormat.format(date)).replace("%winner%", zSpleef.getInstance().getArena().getWinner()))));
            board.updateTitle(Messages.SCOREBOARD_ENDING_TITLE.getString(p));
            board.updateLines(lines);
            return;
        }
        switch (GameState.getState()) {
            case WAITING:
                if (Bukkit.getOnlinePlayers().size() > zSpleef.getInstance().getArena().getMaxPlayers()) {
                    e.setJoinMessage(null);
                    p.sendMessage(Messages.ERROR_REACHEDMAXPLAYERS.getString(p));
                    zSpleef.getInstance().getArena().addSpectator(p);
                    Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.SpectatorLocation"))), 5L);
                    return;
                }
                zSpleef.getInstance().getArena().addPlayer(p);
                if (zSpleef.getInstance().getArena().getPlayers().size() >= zSpleef.getInstance().getArena().getMinPlayers() && !zSpleef.getInstance().getArena().isStarting()) {
                    zSpleef.getInstance().getArena().start(false, false);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Messages.GAME_MINIMUM_PLAYERS_REACHED.getString(online));
                    }
                }
                if (ConfigHandler.getMessages().getBoolean("LeaveItem.Enabled")) p.getInventory().setItem(ConfigHandler.getMessages().getInt("LeaveItem.Slot"), Utils.getLeaveItem());
                Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.WaitingLocation"))), 5L);
                e.setJoinMessage(Messages.ARENA_JOIN_MESSAGE.getString(p).replace("%player%", p.getName()).replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("%max_players%", String.valueOf(zSpleef.getInstance().getArena().getMaxPlayers())));
                return;
            case INGAME:
                Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> p.teleport(Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.SpectatorLocation"))), 5L);
                e.setJoinMessage(null);
                p.sendMessage(Messages.ERROR_INGAMENOWSPECTATOR.getString(p));
                zSpleef.getInstance().getArena().addSpectator(p);
        }

    }
}