package it.zmario.zspleef.listeners;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        if (Utils.isSetup()) return;
        e.setCancelled(true);
        if (!GameState.isState(GameState.INGAME)) return;
        if (ConfigHandler.getConfig().getStringList("AllowedBlocksToBreak").contains(block.getType().toString().toUpperCase())) {
            Main.getInstance().getArena().addBlockToBreaked(block);
            block.setType(Material.AIR);
            p.getInventory().addItem(Utils.getSnowballItem());
            p.updateInventory();
        }
    }
}
