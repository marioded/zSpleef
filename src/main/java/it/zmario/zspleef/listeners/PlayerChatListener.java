package it.zmario.zspleef.listeners;

import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.enums.Messages;
import it.zmario.zspleef.utils.ConfigHandler;
import it.zmario.zspleef.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!ConfigHandler.getConfig().getBoolean("Settings.ChatFormatEnabled")) return;
        Player p = e.getPlayer();
        String format;
        if (zSpleef.getInstance().getArena().isPlayer(p)) {
            format = Messages.CHATFORMAT_PLAYER.getString(p);
        } else {
            format = Messages.CHATFORMAT_SPECTATOR.getString(p);
            e.getRecipients().clear();
            for (UUID spectatorUUID : zSpleef.getInstance().getArena().getSpectators())
                e.getRecipients().add(Bukkit.getPlayer(spectatorUUID));
        }
        //format = format.replace("%", "%%");
        format = format.replace("%level%", String.valueOf(zSpleef.getInstance().getLevels().getLevel(p)));
        format = format.replace("%player%", "%s");
        format = format.replace("%message%", "%s");
        format = Utils.colorize(format);
        e.setFormat(format);
    }
}
