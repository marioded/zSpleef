package it.zmario.zspleef.levels;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.api.events.player.PlayerLevelupEvent;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Expression;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LevelManager {

    public void checkLevelup(Player player) {
        if (getXP(player) >= getNextXP(player)) {
            zSpleef.getInstance().getSql().addStatistic(player, "level", 1);
            player.sendMessage(Messages.LEVELS_LEVELUP.getString(player).replace("%level%", String.valueOf(getLevel(player))));
            Bukkit.getPluginManager().callEvent(new PlayerLevelupEvent(player, getLevel(player), getXP(player)));
            Bukkit.getScheduler().runTaskLater(zSpleef.getInstance(), () -> {
                Utils.sendTitle(player, ConfigHandler.getMessages().getString("Titles.Levelup").replace("%level%", String.valueOf(getLevel(player) + 1)), 20, 60, 20);;
                Utils.playSound(player, "LevelupSound");
            }, 5L);
        }
    }

    public int getXP(Player player) {
        try {
            return zSpleef.getInstance().getSql().getStatistic(player, "xp").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getLevel(Player player) {
        try {
            return zSpleef.getInstance().getSql().getStatistic(player, "level").get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public double getNextXP(Player player) {
        return Expression.eval(ConfigHandler.getConfig().getString("Settings.Levels.LevelupExpression").replace("level", String.valueOf(getLevel(player))).replace("xp", String.valueOf(getXP(player))));
    }
}
