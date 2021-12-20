package it.zmario.zspleef.tasks;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.arena.SpleefArena;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.scoreboard.SpleefBoard;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoreboardUpdateTask extends BukkitRunnable {
    
    private final Main main;

    public ScoreboardUpdateTask(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        for (SpleefBoard board : main.getArena().getBoards().values()) {
            List<String> lines = new ArrayList<>();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            Player player = board.getPlayer();
            if (main.getArena().isStarting()) {
                Messages.SCOREBOARD_STARTING_LINES.getStringList(player).forEach(line -> lines.add(Utils.colorize(line.replaceAll("%seconds%", String.valueOf(main.getArena().getStartTask().seconds)).replaceAll("%player%", player.getName()).replaceAll("%date%", dateFormat.format(date)).replaceAll("%players%", String.valueOf(main.getArena().getPlayers().size())).replaceAll("%max_players%", String.valueOf(main.getArena().getMaxPlayers())))));
                board.updateTitle(Messages.SCOREBOARD_STARTING_TITLE.getString(player));
                board.updateLines(lines);
                continue;
            } else if (main.getArena().isStopping()) {
                Messages.SCOREBOARD_ENDING_LINES.getStringList(player).forEach(line -> lines.add(Utils.colorize(line.replaceAll("%player%", player.getName()).replaceAll("%date%", dateFormat.format(date)).replaceAll("%winner%", main.getArena().getWinner()))));
                board.updateTitle(Messages.SCOREBOARD_ENDING_TITLE.getString(player));
                board.updateLines(lines);
                continue;
            }
            switch (GameState.getState()) {
                case WAITING:
                    Messages.SCOREBOARD_WAITING_LINES.getStringList(player).forEach(line -> lines.add(Utils.colorize(line.replaceAll("%player%", player.getName()).replaceAll("%date%", dateFormat.format(date)).replaceAll("%players%", String.valueOf(main.getArena().getPlayers().size())).replaceAll("%max_players%", String.valueOf(main.getArena().getMaxPlayers())))));
                    board.updateTitle(Messages.SCOREBOARD_WAITING_TITLE.getString(player));
                    board.updateLines(lines);
                    continue;
                case INGAME:
                    Messages.SCOREBOARD_PLAYING_LINES.getStringList(player).forEach(line -> lines.add(Utils.colorize(line.replaceAll("%player%", player.getName()).replaceAll("%date%", dateFormat.format(date)).replaceAll("%players%", String.valueOf(main.getArena().getPlayers().size())).replaceAll("%max_players%", String.valueOf(main.getArena().getMaxPlayers())).replaceAll("%time_left%", main.getArena().getTimeLeft()))));
                    board.updateTitle(Messages.SCOREBOARD_PLAYING_TITLE.getString(player));
                    board.updateLines(lines);
            }
        }
    }
}
