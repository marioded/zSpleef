package it.zmario.zspleef.utils;

import it.zmario.zspleef.Main;
import it.zmario.zspleef.zSpleefAPI;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigHandler {

    private static File config;
    private static YamlConfiguration configFile;

    private static File messages;
    private static YamlConfiguration messagesFile;

    private static File sounds;
    private static YamlConfiguration soundsFile;

    private static File powerups;
    private static YamlConfiguration powerupsFile;

    public static void checkConfig() {
        if (!Main.getInstance().getDataFolder().exists()) {
            Main.getInstance().getDataFolder().mkdirs();
        }
        config = new File(Main.getInstance().getDataFolder(), "config.yml");
        if (!config.exists()) {
            Main.getInstance().saveResource("config.yml", true);
            Debug.info("&aFile 'config.yml' created with success.");
        }
        configFile = YamlConfiguration.loadConfiguration(config);
    }

    public static void reloadConfig() {
        try {
            configFile.save(config);
            configFile = YamlConfiguration.loadConfiguration(config);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.severe("Can't reload the file 'config.yml'.");
        }
    }

    public static YamlConfiguration getConfig() {
        return configFile;
    }

    public static void checkMessages() {
        messages = new File(Main.getInstance().getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            Main.getInstance().saveResource("messages.yml", true);
            Debug.info("&aFile 'messages.yml' created with success.");
        }
        messagesFile = YamlConfiguration.loadConfiguration(messages);
    }

    public static void reloadMessages() {
        try {
            messagesFile.save(messages);
            messagesFile = YamlConfiguration.loadConfiguration(messages);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.severe("Can't reload the file 'messages.yml'.");
        }
    }

    public static YamlConfiguration getMessages() {
        return messagesFile;
    }

    public static void checkSounds() {
        sounds = new File(Main.getInstance().getDataFolder(), "sounds.yml");
        if (!sounds.exists()) {
            Main.getInstance().saveResource("sounds.yml", true);
            Debug.info("&aFile 'sounds.yml' created with success.");
        }
        soundsFile = YamlConfiguration.loadConfiguration(sounds);
    }

    public static void reloadSounds() {
        try {
            soundsFile.save(sounds);
            soundsFile = YamlConfiguration.loadConfiguration(sounds);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.severe("Can't reload the file 'sounds.yml'.");
        }
    }

    public static YamlConfiguration getSounds() {
        return soundsFile;
    }

    public static void checkPowerups() {
        powerups = new File(Main.getInstance().getDataFolder(), "powerups.yml");
        if (!powerups.exists()) {
            Main.getInstance().saveResource("powerups.yml", true);
            Debug.info("&aFile 'powerups.yml' created with success.");
        }
        powerupsFile = YamlConfiguration.loadConfiguration(powerups);
    }

    public static void reloadPowerups() {
        try {
            powerupsFile.save(powerups);
            powerupsFile = YamlConfiguration.loadConfiguration(powerups);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.severe("Can't reload the file 'powerups.yml'.");
        }
    }

    public static YamlConfiguration getPowerups() {
        return powerupsFile;
    }


}
