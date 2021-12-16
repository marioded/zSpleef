package it.zmario.zspleef.utils;

import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TextBuilder {

    /* Credits to @imBuzz */

    private final TextComponent component;

    public TextBuilder(String text) {
        component = new TextComponent(Utils.colorize(text));
    }

    public TextBuilder setHover(String text) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Utils.colorize(text))));
        return this;
    }

    public TextBuilder setUrl(String url) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    public TextBuilder setCommand(String command) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public TextBuilder setSuggestCommand(String command) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return this;
    }

    public void send(Player player) {
        player.spigot().sendMessage(component);
    }

    public void sendToAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.spigot().sendMessage(component);
        }
    }

    public TextComponent getTextComponent() {
        return component;
    }

    public void addExtra(BaseComponent baseComponent) {
        component.addExtra(baseComponent);
    }

    public void addExtra(String text) {
        component.addExtra(text);
    }

    public void addExtra(TextBuilder text) {
        component.addExtra(text.component);
    }


}
