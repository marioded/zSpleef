package it.zmario.zspleef;

import it.zmario.zspleef.api.SpleefAPI;
import it.zmario.zspleef.arena.SpleefArena;
import it.zmario.zspleef.commands.SpleefCommand;
import it.zmario.zspleef.levels.LevelManager;
import it.zmario.zspleef.listeners.*;
import it.zmario.zspleef.arena.Powerup;
import it.zmario.zspleef.sql.SQLManager;
import it.zmario.zspleef.tasks.ScoreboardUpdateTask;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Debug;
import it.zmario.zspleef.utils.Utils;
import lombok.Getter;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class zSpleef extends JavaPlugin {

    @Getter private static zSpleef instance;
    private SpleefArena arena;
    private String version;
    private SQLManager sql;
    private LevelManager levels;
    private SpleefAPI api;

    @Override
    public void onEnable() {
        instance = this;
        api = new SpleefAPI();
        checkConfigs();
        registerListeners(new PlayerJoinListener());
        registerCommands();
        if (Utils.isSetup()) return;
        initDatabase();
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        levels = new LevelManager();
        startTasks();
        registerListeners(new PlayerQuitListener(), new BlockBreakListener(), new PlayerMoveListener(), new PlayerChatListener(), new GeneralListeners());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        loadArena();
        loadPowerups();
    }

    @Override
    public void onDisable() {
        if (Utils.isSetup()) return;
        sql.onDisable();
        for (Block block : getArena().getBlocksBreaked()) {
            block.setType(Material.SNOW_BLOCK);
        }
    }

    private void initDatabase() {
        sql = new SQLManager(this);
    }

    private void checkConfigs() {
        ConfigHandler.checkConfig();
        ConfigHandler.checkMessages();
        ConfigHandler.checkSounds();
        ConfigHandler.checkPowerups();
    }

    private void loadArena() {
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

    private void loadPowerups() {
        if (!ConfigHandler.getConfig().getBoolean("Settings.Powerups.Enabled")) return;
        ConfigurationSection section = ConfigHandler.getPowerups().getConfigurationSection("Powerups");
        if (section.getKeys(false).isEmpty()) return;
        int i = 0;
        for (String key : section.getKeys(false)) {
            i++;
            getArena().getPowerups().add(new Powerup(key));
        }
        Debug.info("Loaded " + i + " powerups with success!");
    }

    private void startTasks() {
        new ScoreboardUpdateTask(this).runTaskTimerAsynchronously(this, 0, 20L);
    }

    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private void registerCommands() {
        getServer().getPluginCommand("spleef").setExecutor(new SpleefCommand());
    }

    public HolographicDisplaysAPI getHolograms() {
        return HolographicDisplaysAPI.get(this);
    }
}
