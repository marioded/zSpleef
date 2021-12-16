package it.zmario.zspleef.nms;

import org.bukkit.entity.Player;

public class v1_16_R3 implements NMS {

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(title == null ? " " : title, subtitle == null ? " " : subtitle, fadeIn, stay, fadeOut);
    }
}