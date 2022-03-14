package it.zmario.zspleef.tasks;

import dev.jcsoftware.jscoreboards.JPerPlayerMethodBasedScoreboard;
import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.api.events.game.GameEndEvent;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeLeftTask extends BukkitRunnable {

    public int timeLeft = ConfigHandler.getConfig().getInt("Settings.TimeLeft");

    @Override
    public void run() {
        if (timeLeft == 0) {
            GameEndEvent endEvent = new GameEndEvent(null);
            Bukkit.getPluginManager().callEvent(endEvent);
            for (Player online : Bukkit.getOnlinePlayers()) {
                Utils.sendTitle(online, ConfigHandler.getMessages().getString("Titles.Game.Lose"), 0, 60, 0);
                online.sendMessage(Messages.GAME_TIME_FINISHED.getString(online));
                online.getInventory().clear();
                zSpleef.getInstance().getArena().restart();
            }
            return;
        }
        List<String> lines = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        JPerPlayerMethodBasedScoreboard board = zSpleef.getInstance().getArena().getScoreboard();
        for (Player online : Bukkit.getOnlinePlayers()) {
            lines.clear();
            Messages.SCOREBOARD_PLAYING_LINES.getStringList(online).forEach(line -> lines.add(Utils.colorize(line.replace("%player%", online.getName()).replace("%date%", dateFormat.format(date)).replace("%players%", String.valueOf(zSpleef.getInstance().getArena().getPlayers().size())).replace("%time_left%", zSpleef.getInstance().getArena().getTimeLeft()))));
            board.setTitle(online, Messages.SCOREBOARD_PLAYING_TITLE.getString(online));
            board.setLines(online, lines);
        }
        timeLeft--;
    }
}
