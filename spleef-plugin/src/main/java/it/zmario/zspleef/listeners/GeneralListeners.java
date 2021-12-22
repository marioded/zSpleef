package it.zmario.zspleef.listeners;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.events.player.PlayerEliminateEvent;
import it.zmario.zspleef.scoreboard.SpleefBoard;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class GeneralListeners implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Snowball && e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            p.setHealth(p.getMaxHealth());
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            e.setCancelled(true);
            if (p.getFoodLevel() < 19.0D)
                p.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onAchievementAwarded(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onProjectile(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball && e.getEntity().getShooter() instanceof Player) {
            BlockIterator iterator = new BlockIterator(e.getEntity().getWorld(), e.getEntity().getLocation().toVector(), e.getEntity().getVelocity().normalize(), 0.0D, 4);
            Block hitBlock = null;
            while (iterator.hasNext()) {
                hitBlock = iterator.next();
                if (hitBlock.getType() != Material.AIR) {
                    break;
                }
            }
            if (hitBlock.getType() == Material.SNOW_BLOCK) {
                Main.getInstance().getArena().addBlockToBreaked(hitBlock);
                hitBlock.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    public void onCrafting(CraftItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getItem() != null && e.getItem().equals(Utils.getLeaveItem())) {
            Utils.sendServer(p, ConfigHandler.getConfig().getString("Settings.LobbyServer"));
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        SpleefBoard board = Main.getInstance().getArena().getBoards().remove(p.getUniqueId());

        if (board != null) {
            board.delete();
        }

        switch (GameState.getState()) {
            case WAITING:
                e.setLeaveMessage(Messages.ARENA_LEAVE_MESSAGE.getString(p).replace("%player%", p.getName()).replaceAll("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size() - 1)).replaceAll("%max_players%", String.valueOf(Main.getInstance().getArena().getMaxPlayers())));
                Main.getInstance().getArena().removePlayer(p);
                return;
            case INGAME:
                e.setLeaveMessage(null);
                if (Main.getInstance().getArena().isPlayer(p) && !Main.getInstance().getArena().isStopping()) {
                    PlayerEliminateEvent playerEliminateEvent = new PlayerEliminateEvent(p);
                    Bukkit.getPluginManager().callEvent(playerEliminateEvent);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Messages.GAME_PLAYER_ELIMINATED_QUIT.getString(p).replaceAll("%player%", p.getName()).replaceAll("%players%", String.valueOf(Main.getInstance().getArena().getPlayers().size())));
                    }
                    Main.getInstance().getArena().checkWin();
                }
                Main.getInstance().getArena().removePlayer(p);
                Main.getInstance().getArena().removeSpectator(p);
        }
    }

}
