package it.zmario.zspleef.arena;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.api.events.player.PlayerPowerupPickupEvent;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.beta.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Random;

public class Powerup {

    private final String identifier;
    private Hologram hologram;

    public Powerup(String identifier) {
        this.identifier = identifier;
    }

    public void spawn() {
        List<Block> blocks = zSpleef.getInstance().getArena().getCuboid().getSnowBlocks();
        Location powerupLocation = blocks.get(new Random().nextInt(blocks.size())).getLocation().add(0, 3.3, 0);
        Hologram hologram = zSpleef.getInstance().getHolograms().createHologram(powerupLocation);
        String text = Utils.colorize(ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".Hologram.TextLine"));
        TextHologramLine textHologramLine = hologram.getLines().appendText(text);
        ItemHologramLine itemHologramLine = hologram.getLines().appendItem(Utils.deserializeItemStack(ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".Hologram.ItemLineMaterial")));
        this.hologram = hologram;
        itemHologramLine.setPickupListener(player -> {
            PlayerPowerupPickupEvent playerPowerupPickupEvent = new PlayerPowerupPickupEvent(player.getPlayer(), this);
            Bukkit.getPluginManager().callEvent(playerPowerupPickupEvent);
            if (playerPowerupPickupEvent.isCancelled()) return;
            destroy();
            if (!ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".DeactivatedMessage").equalsIgnoreCase("none"))
                Bukkit.getScheduler().scheduleSyncDelayedTask(zSpleef.getInstance(), () -> player.getPlayer().sendMessage(getDeactivatedString()), ConfigHandler.getPowerups().getInt("Powerups." + getIdentifier() + ".DeactivateDelay") * 20L);
            if (!ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".ActivatedMessage").equalsIgnoreCase("none")) player.getPlayer().sendMessage(getActivatedString());
            if (!ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Effects").isEmpty()) {
                ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Effects").forEach(string -> player.getPlayer().addPotionEffect(Utils.deserializePotionEffect(string)));
            }
            if (!ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Items").isEmpty()) {
                ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Items").forEach(string -> player.getPlayer().getInventory().addItem(Utils.deserializeItemStack(string)));
            }
            if (!ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Commands").isEmpty()) {
                ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Commands").forEach(string -> {
                    if (string.startsWith("[console] ")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string.substring(10).replace("%player%", player.getPlayer().getName()).replace("%powerup%", text));
                    } else {
                        player.getPlayer().performCommand(string.replace("%player%", player.getPlayer().getName()).replace("%powerup%", text));
                    }
                });
            }
        });
    }

    public void destroy() {
        hologram.delete();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getActivatedString() {
        return Utils.colorize(ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".ActivatedMessage"));
    }

    public String getDeactivatedString() {
        return Utils.colorize(ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".DeactivatedMessage"));
    }
}
