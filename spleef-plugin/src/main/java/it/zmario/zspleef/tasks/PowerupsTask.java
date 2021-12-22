package it.zmario.zspleef.tasks;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.arena.Powerup;
import it.zmario.zspleef.events.game.GamePowerupSpawnEvent;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

public class PowerupsTask extends BukkitRunnable {

    @Override
    public void run() {
        for (Iterator<Powerup> iterator = Main.getInstance().getArena().getActivePowerups().iterator(); iterator.hasNext();) {
            Powerup powerup = iterator.next();
            iterator.remove();
            powerup.destroy();
        }
        Powerup powerup = Utils.random(Main.getInstance().getArena().getPowerups());
        GamePowerupSpawnEvent gamePowerupSpawnEvent  = new GamePowerupSpawnEvent(powerup);
        Bukkit.getPluginManager().callEvent(gamePowerupSpawnEvent);
        if (gamePowerupSpawnEvent.isCancelled()) return;
        Main.getInstance().getArena().getActivePowerups().add(powerup);
        powerup.spawn();
    }
}
