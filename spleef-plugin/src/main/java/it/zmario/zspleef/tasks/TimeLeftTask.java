package it.zmario.zspleef.tasks;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.events.game.GameEndEvent;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeLeftTask extends BukkitRunnable {

    public int timeLeft = ConfigHandler.getConfig().getInt("Settings.TimeLeft");

    @Override
    public void run() {
        if (timeLeft == 0) {
            GameEndEvent endEvent = new GameEndEvent(null);
            Bukkit.getPluginManager().callEvent(endEvent);
            for (Player online : Bukkit.getOnlinePlayers()) {
                Utils.sendTitle(online, "Titles.Game.Lose", 0, 60, 0);
                online.sendMessage(Messages.GAME_TIME_FINISHED.getString(online));
                online.getInventory().clear();
                Main.getInstance().getArena().restart();
            }
            return;
        }
        timeLeft--;
    }
}
