package it.zmario.zspleef.listeners;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.api.events.game.GameEndEvent;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.api.events.player.PlayerEliminateEvent;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.BlockIterator;

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
                zSpleef.getInstance().getArena().addBlockToBreaked(hitBlock);
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

        switch (GameState.getState()) {
            case WAITING:
                if (zSpleef.getInstance().getArena().isPlayer(p)) {
                    e.setLeaveMessage(Messages.ARENA_LEAVE_MESSAGE.getString(p).replace("%player%", p.getName()).replace("%online_players%", String.valueOf(Bukkit.getOnlinePlayers().size() - 1)).replace("%max_players%", String.valueOf(zSpleef.getInstance().getArena().getMaxPlayers())));
                    zSpleef.getInstance().getArena().removePlayer(p);
                } else {
                    e.setLeaveMessage(null);
                    zSpleef.getInstance().getArena().removeSpectator(p);
                }
                return;
            case INGAME:
                e.setLeaveMessage(null);
                if (zSpleef.getInstance().getArena().isPlayer(p) && !zSpleef.getInstance().getArena().isStopping()) {
                    PlayerEliminateEvent playerEliminateEvent = new PlayerEliminateEvent(p);
                    Bukkit.getPluginManager().callEvent(playerEliminateEvent);
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.sendMessage(Messages.GAME_PLAYER_ELIMINATED_QUIT.getString(p).replace("%player%", p.getName()).replace("%players%", String.valueOf(zSpleef.getInstance().getArena().getPlayers().size())));
                    }
                    zSpleef.getInstance().getArena().checkWin();
                }
                zSpleef.getInstance().getArena().removePlayer(p);
                zSpleef.getInstance().getArena().removeSpectator(p);
        }
    }

    @EventHandler
    public void onPlayerEliminate(PlayerEliminateEvent e) {
        Player p = e.getPlayer();
        int gainedXp = ConfigHandler.getConfig().getInt("Settings.Levels.Experience.Lose");
        zSpleef.getInstance().getSql().addStatistic(p, "loses", 1);
        zSpleef.getInstance().getSql().addStatistic(p, "xp", gainedXp);
        p.sendMessage(Messages.LEVELS_GAIN_LOSEXP.getString(p).replace("%xp%", String.valueOf(gainedXp)));
        Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> zSpleef.getInstance().getLevels().checkLevelup(p), 10L);

    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Player p = e.getWinner();
        if (p != null) {
            int gainedXp = ConfigHandler.getConfig().getInt("Settings.Levels.Experience.Win");
            zSpleef.getInstance().getSql().addStatistic(p, "wins", 1);
            zSpleef.getInstance().getSql().addStatistic(p, "xp", gainedXp);
            p.sendMessage(Messages.LEVELS_GAIN_WINXP.getString(p).replace("%xp%", String.valueOf(gainedXp)));
            Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> zSpleef.getInstance().getLevels().checkLevelup(p), 10L);
        }
    }

}
