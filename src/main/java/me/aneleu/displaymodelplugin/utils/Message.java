package me.aneleu.displaymodelplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Message {

    public static void sendInfoMessage(@NotNull Player player, String message) {
        player.sendMessage(Component.text(message, NamedTextColor.GREEN));
    }

    public static void sendSuggestionMessage(@NotNull Player player, String message) {
        player.sendMessage(Component.text(message, NamedTextColor.GRAY));
    }

    public static void sendErrorMessage(@NotNull Player player, String message) {
        player.sendMessage(Component.text(message, NamedTextColor.RED));
    }

}
