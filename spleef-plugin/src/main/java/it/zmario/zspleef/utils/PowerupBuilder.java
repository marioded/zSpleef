package it.zmario.zspleef.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PowerupBuilder {

    private final String identifier;
    private final ItemStack itemStack;

    public PowerupBuilder(String identifier) {
        this.identifier = identifier;
        this.itemStack = new ItemBuilder(Material.valueOf(ConfigHandler.getMessages().getString("Powerups." + identifier + ".ItemMaterial")))
                .getItemStack();
    }

    public String getActivatedString() {
        return Utils.colorize(ConfigHandler.getMessages().getString("Powerups." + identifier + ".ActivatedMessage"));
    }

    public String getDeactivatedString() {
        return Utils.colorize(ConfigHandler.getMessages().getString("Powerups." + identifier + ".DeactivatedMessage"));
    }

    public ItemStack getItemStack() {
        itemStack.setItemMeta(itemStack.getItemMeta());
        return itemStack;
    }
}
