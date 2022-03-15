package it.zmario.zspleef.listeners;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();
        if (GameState.isState(GameState.INGAME) && zSpleef.getInstance().getArena().isPlayer(p) && !zSpleef.getInstance().getArena().isStopping() && block.getType() == Material.SNOW_BLOCK) {
            zSpleef.getInstance().getArena().addBlockToBreaked(block);
            block.setType(Material.AIR);
            if (ConfigHandler.getMessages().getBoolean("SnowballItem.Enabled")) {
                int i = 0;
                for (ItemStack item : p.getInventory().getContents()) {
                    if (item == null) continue;
                    if (item.getType() != Material.SNOW_BALL) continue;
                    i = i + item.getAmount();
                }
                if (i <= ConfigHandler.getConfig().getInt("Settings.MaxSnowballs") - 1) {
                    p.getInventory().addItem(Utils.getSnowballItem());
                    p.updateInventory();
                }
            }
        }
        e.setCancelled(true);
    }
}
