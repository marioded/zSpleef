package it.zmario.zspleef.support;

import it.zmario.zspleef.zSpleef;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "zspleef";
    }

    @Override
    public @NotNull String getAuthor() {
        return "zMario";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.equalsIgnoreCase("level")) {
            return String.valueOf(zSpleef.getInstance().getApi().getLevel(player));
        }

        if (params.equalsIgnoreCase("xp")) {
            return String.valueOf(zSpleef.getInstance().getApi().getXP(player));
        }

        if (params.equalsIgnoreCase("xp_needed")) {
            return String.valueOf(zSpleef.getInstance().getLevels().getNextXP(player));
        }

        return null;
    }
}
