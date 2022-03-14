package it.zmario.zspleef.commands;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.GameState;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.TextBuilder;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpleefCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.colorize("&8• &cThis command can be executed only by player!"));
            return false;
        }
        Player p = (Player) sender;
        if (Utils.isSetup()) {
            if (!p.hasPermission("zspleef.admin")) {
                p.sendMessage(Messages.ERROR_NOPERMISSION.getString(p));
                return false;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("setwaitinglocation")) {
                    ConfigHandler.getConfig().set("Arena.WaitingLocation", Utils.serializeLocation(p.getLocation()));
                    ConfigHandler.reloadConfig();
                    p.sendMessage(Utils.colorize("&8• &aThe waiting location was set with success!"));
                }
                if (args[0].equalsIgnoreCase("setspectlocation")) {
                    ConfigHandler.getConfig().set("Arena.SpectatorLocation", Utils.serializeLocation(p.getLocation()));
                    ConfigHandler.reloadConfig();
                    p.sendMessage(Utils.colorize("&8• &aThe spectator location was set with success!"));
                }
                if (args[0].equalsIgnoreCase("exit")) {
                    if (ConfigHandler.getConfig().getString("Arena.WaitingLocation") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe waiting location has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.SpectatorLocation") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe spectator location has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.Borders.Pos1") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe border position number 1 has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.Borders.Pos2") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe border position number 2 has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.MinPlayers") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe arena minimum players has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.MaxPlayers") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe arena maximum players has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.MaxPlayers") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe arena maximum players has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getString("Arena.DeathLevel") == null) {
                        p.sendMessage(Utils.colorize("&8• &cThe arena death level has not been set!"));
                        return false;
                    }
                    if (ConfigHandler.getConfig().getConfigurationSection("Arena.Spawns") == null || ConfigHandler.getConfig().getConfigurationSection("Arena.Spawns").getKeys(false).size() != ConfigHandler.getConfig().getInt("Arena.MaxPlayers")) {
                        p.sendMessage(Utils.colorize("&8• &cThe arena spawns have not been set!"));
                        return false;
                    }
                    ConfigHandler.getConfig().set("SetupMode", false);
                    ConfigHandler.reloadConfig();
                    p.sendMessage(Utils.colorize("&8• &aYou exited the arena and it has been saved! The server will restart in 5 seconds..."));
                    Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), Bukkit::shutdown, 20 * 5);
                    return true;
                }
            }
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("setspawn")) {
                    try {
                        int number = Integer.parseInt(args[1]);
                        if (number < 0) {
                            p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                            return false;
                        }
                        int maxplayers = ConfigHandler.getConfig().getInt("Arena.MaxPlayers");
                        if (maxplayers == 0) {
                            p.sendMessage(Utils.colorize("&8• &cYou need to set the max players first!"));
                            return false;
                        }
                        if (number > maxplayers) {
                            p.sendMessage(Utils.colorize("&8• &cYou can set up to &l" + maxplayers + " spawns&c! If you want to edit this value, please do &l/spleef setmaxplayers <number>&c."));
                            return false;
                        }
                        ConfigHandler.getConfig().set("Arena.Spawns." + number, Utils.serializeLocation(p.getLocation()));
                        ConfigHandler.reloadConfig();
                        p.sendMessage(Utils.colorize("&8• &aThe spawn number &l" + number + " &awas set with success!"));
                    } catch (NumberFormatException e) {
                        p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("removespawn")) {
                    try {
                        int number = Integer.parseInt(args[1]);
                        if (number < 0) {
                            p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                            return false;
                        }
                        if (ConfigHandler.getConfig().getString("Arena.Spawns." + number) == null) {
                            p.sendMessage(Utils.colorize("&8• &cThe spawn number &l" + number + " &cdoesn't exist!"));
                            return false;
                        }
                        ConfigHandler.getConfig().set("Arena.Spawns." + number, null);
                        ConfigHandler.reloadConfig();
                        p.sendMessage(Utils.colorize("&8• &aThe spawn number &l" + number + " &awas removed with success!"));
                    } catch (NumberFormatException e) {
                        p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("setminplayers")) {
                    try {
                        int number = Integer.parseInt(args[1]);
                        if (number < 0) {
                            p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                            return false;
                        }
                        ConfigHandler.getConfig().set("Arena.MinPlayers", number);
                        ConfigHandler.reloadConfig();
                        p.sendMessage(Utils.colorize("&8• &aThe minimum players number was set to &l" + number + " &awith success!"));
                    } catch (NumberFormatException e) {
                        p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("setmaxplayers")) {
                    try {
                        int number = Integer.parseInt(args[1]);
                        if (number < 0) {
                            p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                            return false;
                        }
                        ConfigHandler.getConfig().set("Arena.MaxPlayers", number);
                        ConfigHandler.reloadConfig();
                        p.sendMessage(Utils.colorize("&8• &aThe maximum players number was set to &l" + number + " &awith success!"));
                    } catch (NumberFormatException e) {
                        p.sendMessage(Utils.colorize("&8• &cThe spawn must be a positive number!"));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("setborder")) {
                    try {
                        int number = Integer.parseInt(args[1]);
                        if (number == 1 || number == 2) {
                            ConfigHandler.getConfig().set("Arena.Borders.Pos" + number, Utils.serializeLocation(p.getLocation()));
                            ConfigHandler.reloadConfig();
                            p.sendMessage(Utils.colorize("&8• &aThe border position number &l" + number + " &awas set with success!"));
                            return true;
                        }
                        p.sendMessage(Utils.colorize("&8• &cThe border position need to be &l1 &cor &l2&c!"));
                        return false;
                    } catch (NumberFormatException e) {
                        p.sendMessage(Utils.colorize("&8• &cThe border position need to be &l1 &cor &l2&c!"));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("setdeathlevel")) {
                    try {
                        int number = Integer.parseInt(args[1]);
                        ConfigHandler.getConfig().set("Arena.DeathLevel", number);
                        ConfigHandler.reloadConfig();
                        p.sendMessage(Utils.colorize("&8• &aThe death level was set to &l" + number + " &awith success!"));
                        return false;
                    } catch (NumberFormatException e) {
                        p.sendMessage(Utils.colorize("&8• &cThe death level must be a number!"));
                        return true;
                    }
                }
            }
            p.sendMessage(Utils.colorize("&ezSpleef &7v" + zSpleef.getInstance().getDescription().getVersion() + " by &bzMario &7- &cSetup Mode"));
            new TextBuilder("&8• &7/spleef setwaitinglocation &7- Sets the spleef waiting location.").setCommand("/spleef setwaitinglocation").setHover("&8» &eClick to run the command!").send(p);
            new TextBuilder("&8• &7/spleef setspectlocation &7- Sets the spleef spectator location.").setCommand("/spleef setspectlocation").setHover("&8» &eClick to run the command!").send(p);
            new TextBuilder("&8• &7/spleef setspawn <number> &7- Adds a new player spawn.").setSuggestCommand("/spleef setspawn ").setHover("&8» &eClick to suggest the command!").send(p);
            new TextBuilder("&8• &7/spleef removespawn <number> &7- Removes an existing player spawn.").setSuggestCommand("/spleef removespawn ").setHover("&8» &eClick to suggest the command!").send(p);
            new TextBuilder("&8• &7/spleef setminplayers <number> &7- Sets minimum players to start the game.").setSuggestCommand("/spleef setminplayers ").setHover("&8» &eClick to suggest the command!").send(p);
            new TextBuilder("&8• &7/spleef setmaxplayers <number> &7- Sets the maximum players of the game.").setSuggestCommand("/spleef setmaxplayers ").setHover("&8» &eClick to suggest the command!").send(p);
            new TextBuilder("&8• &7/spleef setborder <1-2> &7- Sets the spleef border position.").setSuggestCommand("/spleef setborder ").setHover("&8» &eClick to suggest the command!").send(p);
            new TextBuilder("&8• &7/spleef setdeathlevel <y-coordinate> &7- Sets the spleef death level.").setSuggestCommand("/spleef setdeathlevel ").setHover("&8» &eClick to suggest the command!").send(p);
            new TextBuilder("&8• &7/spleef exit &7- Exits the setup and restarts the server.").setCommand("/spleef exit").setHover("&8» &eClick to run the command!").send(p);
            return true;
        }
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("start") && p.hasPermission("zspleef.start")) {
                if (GameState.getState() == GameState.INGAME) {
                    p.sendMessage(Messages.FORCESTART_ALREADYSTARTED.getString(p));
                    return false;
                }
                boolean debug = args.length > 1 && args[1].equalsIgnoreCase("debug") && p.hasPermission("zspleef.start.debug");
                p.sendMessage(Messages.FORCESTART_STARTING.getString(p));
                if (zSpleef.getInstance().getArena().isStarting()) zSpleef.getInstance().getArena().getStartTask().cancel();
                zSpleef.getInstance().getArena().start(true, debug);
                return false;
            }
            if (args[0].equalsIgnoreCase("stop") && p.hasPermission("zspleef.stop")) {
                if (zSpleef.getInstance().getArena().isStopping()) {
                    p.sendMessage(Messages.FORCESTOP_ALREADYSTOPPED.getString(p));
                    return false;
                }
                if (GameState.isState(GameState.WAITING)) {
                    p.sendMessage(Messages.FORCESTOP_NOTSTARTED.getString(p));
                    return false;
                }
                p.sendMessage(Messages.FORCESTOP_STOPPING.getString(p));
                zSpleef.getInstance().getArena().restart();
                return false;
            }
        }
        p.sendMessage(Utils.colorize("&6&lzSpleef &7v" + zSpleef.getInstance().getDescription().getVersion() + " by &bzMario"));
        if (p.hasPermission("zspleef.admin")) {
            new TextBuilder("&8• &7/spleef start &7- Force starts the game.").setCommand("/spleef forcestart").setHover("&8» &eClick to run the command!").send(p);
            new TextBuilder("&8• &7/spleef stop &7- Force stops the game.").setCommand("/spleef forcestop").setHover("&8» &eClick to run the command!").send(p);
        }

        return false;
    }
}
