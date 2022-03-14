package it.zmario.zspleef.utils;

import it.zmario.zspleef.zSpleef;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String colorize(String msg) {
        if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
            Pattern pattern = Pattern.compile("#[a-fA-F-0-9]{6}");
            Matcher match = pattern.matcher(msg);
            while (match.find()) {
                String color = msg.substring(match.start(), match.end());
                msg = msg.replace(color, ChatColor.valueOf(color) + "");
                match = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static boolean containsIllegals(String string) {
        Matcher matcher = Pattern.compile("[~#@*+%{}<>\\[\\]|^]").matcher(string);
        return matcher.find();
    }

    public static ItemStack deserializeItemStack(String path) {
        if (path == null || path.trim().equals("")) {
            return null;
        }
        String[] parts = path.split(";");
        if (parts.length == 1) {
            return new ItemStack(Material.matchMaterial(parts[0]), 1);
        }
        if (parts.length == 2) {
            return new ItemStack(Material.matchMaterial(parts[0]), 1, Short.parseShort(parts[1]));
        }
        if (parts.length == 3) {
            return new ItemStack(Material.matchMaterial(parts[0]), Integer.parseInt(parts[1]), Short.parseShort(parts[2]));
        }
        return null;
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

    public static PotionEffect deserializePotionEffect(String path) {
        if (path == null || path.trim().equals("")) {
            return null;
        }
        String[] parts = path.split(";");
        if (parts.length == 3) {
            return new PotionEffect(PotionEffectType.getByName(parts[0].toUpperCase()), Integer.parseInt(parts[1]) * 20, Integer.parseInt(parts[2]));
        }
        return null;
    }

    public static String serializeLocation(Location location) {
        if (location == null) {
            return "";
        }
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    public static void sendTitle(Player player, String strings, int fadeIn, int stay, int fadeOut) {
        try { // Working on to not use packets for 1.11+
            String[] titles = Utils.colorize(strings).split(";");
            Class<?> PacketPlayOutTitleClass = getNMSClass("PacketPlayOutTitle");
            Class<?> IChatBaseComponentClass = getNMSClass("IChatBaseComponent");

            Constructor<?> constructor = PacketPlayOutTitleClass.getConstructor(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0], IChatBaseComponentClass, int.class, int.class, int.class);

            Object titleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + titles[0] + "\"}");
            Object subTitleComponent = IChatBaseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + titles[1] + "\"}");

            Object titlePacket = constructor.newInstance(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0].getField("TITLE").get(null), titleComponent, fadeIn, stay, fadeOut);
            Object subTitlePacket = constructor.newInstance(PacketPlayOutTitleClass
                    .getDeclaredClasses()[0].getField("SUBTITLE")
                    .get(null), subTitleComponent, fadeIn, stay, fadeOut);

            sendPacket(player, titlePacket);
            sendPacket(player, subTitlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSound(Player player, String soundPath) {
        if (!ConfigHandler.getConfig().getBoolean("Settings.SoundsEnabled")) return;
        String[] sound = ConfigHandler.getSounds().getString(soundPath).split(";");
        if (sound[0].equalsIgnoreCase("none")) return;
        try {
            player.playSound(player.getLocation(), Sound.valueOf(sound[0].toUpperCase()), Float.parseFloat(sound[1]), Float.parseFloat(sound[2]));
        } catch (IllegalArgumentException e) {
            Debug.severe("Can't play the sound " + sound[0].toUpperCase() + " &cto " + player.getName() + ". The sound doesn't exist!");
        }
    }

    public static boolean isSetup() {
        return ConfigHandler.getConfig().getBoolean("SetupMode");
    }

    public static ItemStack getSnowballItem() {
        ItemStack itemStack = new ItemStack(Material.SNOW_BALL, ConfigHandler.getMessages().getInt("SnowballItem.Amount"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utils.colorize(ConfigHandler.getMessages().getString("SnowballItem.Name")));
        ArrayList<String> lore = new ArrayList<>();
        ConfigHandler.getMessages().getStringList("SnowballItem.Lore").forEach(string -> lore.add(Utils.colorize(string)));
        meta.setLore(lore);
        if (ConfigHandler.getMessages().getBoolean("SnowballItem.Enchanted")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getLeaveItem() {
        ItemStack itemStack = new ItemStack(Material.valueOf(ConfigHandler.getMessages().getString("LeaveItem.Material").toUpperCase()), ConfigHandler.getMessages().getInt("LeaveItem.Amount"), (short) ConfigHandler.getMessages().getInt("LeaveItem.Data"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utils.colorize(ConfigHandler.getMessages().getString("LeaveItem.Name")));
        ArrayList<String> lore = new ArrayList<>();
        ConfigHandler.getMessages().getStringList("LeaveItem.Lore").forEach(string -> lore.add(Utils.colorize(string)));
        meta.setLore(lore);
        if (ConfigHandler.getMessages().getBoolean("LeaveItem.Enchanted")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack getShovelItem() {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(ConfigHandler.getMessages().getString("ShovelItem.Material").toUpperCase()), ConfigHandler.getMessages().getInt("ShovelItem.Amount"), (short) ConfigHandler.getMessages().getInt("ShovelItem.Data"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Utils.colorize(ConfigHandler.getMessages().getString("ShovelItem.Name")));
        ArrayList<String> lore = new ArrayList<>();
        ConfigHandler.getMessages().getStringList("ShovelItem.Lore").forEach(string -> lore.add(Utils.colorize(string)));
        if (ConfigHandler.getMessages().getBoolean("ShovelItem.Enchanted")) {
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
        }
        meta.setLore(lore);
        itemStack.getItemMeta().addEnchant(Enchantment.DURABILITY, 999, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
        player.sendPluginMessage(zSpleef.getInstance(), "BungeeCord", byteArrayOutputStream.toByteArray());
    }

    public static void setSetupMode(boolean value) {
        ConfigHandler.getConfig().set("SetupMode", value);
        ConfigHandler.reloadConfig();
        Bukkit.shutdown();
    }

    public static void spawnFireworks(Location location, int amount) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();
        for (int i = 0; i < amount; i++) {
            Firework fw2 = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    public static <T> T random(List<T> paramList) {
        return paramList.get(ThreadLocalRandom.current().nextInt(paramList.size()));
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + zSpleef.getInstance().getVersion() + "." + name);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            Debug.severe("Error while finding the class " + name);
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
                    .invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Work in progress
    public static void addDefaultSound(String path, String v18, String v112,  String v113) {
        switch (Main.getInstance().getVersion()) {
            case "v1_8_R3":
            case "v1_9_R2":
            case "v1_10_R1":
            case "v1_11_R1":
                ConfigHandler.getSounds().addDefault(path + ".Sound", v18);
            case "v1_12_R1":
                ConfigHandler.getSounds().addDefault(path + ".Sound", v112);
            default:
                ConfigHandler.getSounds().addDefault(path + ".Sound", v113);
                ConfigHandler.getSounds().addDefault(path + ".Volume", 1);
                ConfigHandler.getSounds().addDefault(path + ".Pitch", 1);
        }
    } */
}