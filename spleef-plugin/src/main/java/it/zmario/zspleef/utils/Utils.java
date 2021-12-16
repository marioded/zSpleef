package it.zmario.zspleef.utils;

import it.zmario.zspleef.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean containsIllegals(String string) {
        Matcher matcher = Pattern.compile("[~#@*+%{}<>\\[\\]|^]").matcher(string);
        return matcher.find();
    }

    public static Location deserializeLocation(String path) {
        if (path == null || path.trim().equals("")) {
            return null;
        }
        String[] parts = path.split(";");
        if (parts.length == 6) {
            return new Location(Bukkit.getServer().getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        }
        return null;
    }

    public static String serializeLocation(Location location) {
        if (location == null) {
            return "";
        }
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static boolean isSetup() {
        return ConfigHandler.getConfig().getBoolean("SetupMode");
    }

    public static ItemStack getSnowballItem() {
        ItemStack itemStack = new ItemStack(Material.SNOW_BALL, ConfigHandler.getMessages().getInt("SnowballItem.Amount"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utils.colorize(ConfigHandler.getMessages().getString("SnowballItem.Name")));
        meta.setLore(ConfigHandler.getMessages().getStringList("SnowballItem.Lore"));
        if (ConfigHandler.getMessages().getBoolean("SnowballItem.Enchanted")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getLeaveItem() {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(ConfigHandler.getMessages().getString("LeaveItem.Material").toUpperCase()), ConfigHandler.getMessages().getInt("LeaveItem.Amount"), (short) ConfigHandler.getMessages().getInt("LeaveItem.Data"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utils.colorize(ConfigHandler.getMessages().getString("LeaveItem.Name")));
        meta.setLore(ConfigHandler.getMessages().getStringList("LeaveItem.Lore"));
        if (ConfigHandler.getMessages().getBoolean("LeaveItem.Enchanted")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void sendServer(Player player, String server) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(Main.getInstance(), "BungeeCord", byteArrayOutputStream.toByteArray());
    }

    public static void setSetupMode(boolean value) {
        ConfigHandler.getConfig().set("SetupMode", value);
        ConfigHandler.reloadConfig();
        Bukkit.shutdown();
    }
}