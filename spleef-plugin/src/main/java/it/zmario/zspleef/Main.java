package it.zmario.zspleef;

import it.zmario.zspleef.arena.SpleefArena;
import it.zmario.zspleef.commands.SpleefCommand;
import it.zmario.zspleef.nms.*;
import it.zmario.zspleef.listeners.*;
import it.zmario.zspleef.tasks.ScoreboardUpdateTask;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Debug;
import it.zmario.zspleef.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter private NMS nms;
    @Getter private static Main instance;
    @Getter private SpleefArena arena;
    @Getter private String version;

    @Override
    public void onEnable() {
        instance = this;
        if (!setupNMS()) {
            Debug.severe("Can't load support for the your server version (" + version + ")! Please use a compatible version from 1.8.8 to 1.18.1. If you have any problems please contact me (zMario).");
        } else {
            Debug.info("&aSupport for version &l" + version + " &aloaded!");
        }
        checkConfigs();
        registerListeners(new PlayerJoinListener());
        if (!Utils.isSetup())
            registerListeners(new PlayerQuitListener(), new BlockBreakListener(), new PlayerMoveListener(), new GeneralListeners());
        registerCommands();
        loadArena();
        startTasks();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void startTasks() {
        new ScoreboardUpdateTask(this).runTaskTimer(this, 0, 20L);
    }

    @Override
    public void onDisable() {
        if (Utils.isSetup()) return;
        for (Block block : getArena().getBlocksBreaked()) {
            block.setType(Material.SNOW_BLOCK);
        }
    }

    private void checkConfigs() {
        ConfigHandler.checkConfig();
        ConfigHandler.checkMessages();
        ConfigHandler.checkSounds();
    }

    private void loadArena() {
        if (ConfigHandler.getConfig().getBoolean("SetupMode")) return;
        boolean error = false;
        if (ConfigHandler.getConfig().getString("Arena.WaitingLocation") == null) {
            Debug.severe("&8• &cThe waiting location has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.SpectatorLocation") == null) {
            Debug.severe("&8• &cThe spectator location has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.Borders.Pos1") == null) {
            Debug.severe("&8• &cThe border position number 1 has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.Borders.Pos2") == null) {
            Debug.severe("&8• &cThe border position number 2 has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.MinPlayers") == null) {
            Debug.severe("&8• &cThe arena minimum players has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.MaxPlayers") == null) {
            Debug.severe("&8• &cThe arena maximum players has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.MaxPlayers") == null) {
            Debug.severe("&8• &cThe arena maximum players has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getString("Arena.DeathLevel") == null) {
            Debug.severe("&8• &cThe arena death level has not been set!");
            error = true;
        }
        if (ConfigHandler.getConfig().getConfigurationSection("Arena.Spawns") == null || ConfigHandler.getConfig().getConfigurationSection("Arena.Spawns").getKeys(false).size() != ConfigHandler.getConfig().getInt("Arena.MaxPlayers")) {
            Debug.severe("&8• &cThe arena spawns have not been set!");
            error = true;
        }
        if (error) {
            Debug.severe("&8• The plugin will be disabled and set in setup mode.");
            Utils.setSetupMode(true);
            return;
        }
        arena = new SpleefArena(this, Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.Borders.Pos1")), Utils.deserializeLocation(ConfigHandler.getConfig().getString("Arena.Borders.Pos2")));
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void registerCommands() {
        getServer().getPluginCommand("spleef").setExecutor(new SpleefCommand());
    }

    private boolean setupNMS() {
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
        switch (version) {
            case "v1_8_R3":
                nms = new v1_8_R3();
                break;
            case "v1_9_R2":
                nms = new v1_9_R2();
                break;
            case "v1_10_R1":
                nms = new v1_10_R1();
                break;
            case "v1_11_R1":
                nms = new v1_11_R1();
                break;
            case "v1_12_R1":
                nms = new v1_12_R1();
                break;
            case "v1_13_R2":
                nms = new v1_13_R2();
                break;
            case "v1_14_R1":
                nms = new v1_14_R1();
                break;
            case "v1_15_R1":
                nms = new v1_15_R1();
                break;
            case "v1_16_R1":
                nms = new v1_16_R1();
                break;
            case "v1_16_R2":
                nms = new v1_16_R2();
                break;
            case "v1_16_R3":
                nms = new v1_16_R3();
                break;
            case "v1_17_R1":
                nms = new v1_17_R1();
                break;
            case "v1_18_R1":
                nms = new v1_18_R1();
                break;
        }
        return (nms != null);
    }
}
