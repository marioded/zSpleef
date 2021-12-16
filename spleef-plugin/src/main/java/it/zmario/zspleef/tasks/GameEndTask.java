package it.zmario.zspleef.tasks;

import it.zmario.zspleef.Main;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class GameEndTask extends BukkitRunnable {

    @Override
    public void run() {
        if (!Main.getInstance().getArena().getBlocksBreaked().isEmpty()) {
            for (Block block : Main.getInstance().getArena().getBlocksBreaked()) {
                block.setType(block.getType());
            }
        }
        //Bukkit.shutdown();
    }

}
