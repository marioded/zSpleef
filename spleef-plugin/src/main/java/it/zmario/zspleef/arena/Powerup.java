package it.zmario.zspleef.arena;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.events.player.PlayerPowerupPickupEvent;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.beta.hologram.line.TextHologramLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class Powerup {

    private final String identifier;
    private Hologram hologram;

    public Powerup(String identifier) {
        this.identifier = identifier;
    }

    public void spawn() {
        List<Block> blocks = Main.getInstance().getArena().getCuboid().getSnowBlocks();
        Location powerupLocation = blocks.get(new Random().nextInt(blocks.size())).getLocation().add(0, 3.5, 0);
        Hologram hologram = Main.getInstance().getHolograms().createHologram(powerupLocation);
        TextHologramLine textHologramLine = hologram.getLines().appendText(Utils.colorize(ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".Hologram.TextLine")));
        ItemHologramLine itemHologramLine = hologram.getLines().appendItem(Utils.deserializeItemStack(ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".Hologram.ItemLineMaterial")));
        this.hologram = hologram;
        itemHologramLine.setPickupListener(player -> {
            PlayerPowerupPickupEvent playerPowerupPickupEvent = new PlayerPowerupPickupEvent(player.getPlayer(), this);
            Bukkit.getPluginManager().callEvent(playerPowerupPickupEvent);
            if (playerPowerupPickupEvent.isCancelled()) return;
            destroy();
            if (!ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".DeactivatedMessage").equalsIgnoreCase("none")) Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> player.getPlayer().sendMessage(getDeactivatedString()), Integer.parseInt(ConfigHandler.getMessages().getString("Powerups.JumpBoost.Effect").split(";")[1]) * 20L);
            if (!ConfigHandler.getPowerups().getString("Powerups." + getIdentifier() + ".ActivatedMessage").equalsIgnoreCase("none")) player.getPlayer().sendMessage(getActivatedString());
            if (!ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Effects").isEmpty()) {
                ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Effects").forEach(string -> player.getPlayer().addPotionEffect(Utils.deserializePotionEffect(string)));
            }
            if (!ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Items").isEmpty()) {
                ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Items").forEach(string -> player.getPlayer().getInventory().addItem(Utils.deserializeItemStack(string)));
            }
            if (!ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Commands").isEmpty()) {
                ConfigHandler.getPowerups().getStringList("Powerups." + identifier + ".Commands").forEach(string -> {
                    if (string.startsWith("[console]")) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), string);
                    } else {
                        player.getPlayer().performCommand(string);
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
