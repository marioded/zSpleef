package it.zmario.zspleef.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder setName(String name) {
        itemStack.getItemMeta().setDisplayName(Utils.colorize(name));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        List<String> loreString = new ArrayList<>();
        lore.forEach(string -> loreString.add(Utils.colorize(string)));
        itemStack.getItemMeta().setLore(loreString);
        return this;
    }

    public ItemBuilder setEnchanted() {
        itemStack.getItemMeta().addEnchant(Enchantment.DURABILITY, 1, false);
        itemStack.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setUnbreakable() {
        itemStack.getItemMeta().addEnchant(Enchantment.DURABILITY, 999, true);
        return this;
    }

    public ItemStack getItemStack() {
        itemStack.setItemMeta(itemStack.getItemMeta());
        return itemStack;
    }
}
