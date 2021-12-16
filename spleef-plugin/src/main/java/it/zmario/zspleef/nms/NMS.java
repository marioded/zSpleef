package it.zmario.zspleef.nms;

import org.bukkit.entity.Player;

public interface NMS {

    void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);

}
