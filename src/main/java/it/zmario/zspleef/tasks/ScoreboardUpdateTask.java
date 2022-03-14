package it.zmario.zspleef.tasks;

import it.zmario.zspleef.zSpleef;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdateTask extends BukkitRunnable {
    
    private final zSpleef zSpleef;

    public ScoreboardUpdateTask(zSpleef zSpleef) {
        this.zSpleef = zSpleef;
    }

    @Override
    public void run() {
        zSpleef.getArena().getScoreboard().updateScoreboard();
    }
}
