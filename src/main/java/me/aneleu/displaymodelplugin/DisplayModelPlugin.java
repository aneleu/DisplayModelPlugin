package me.aneleu.displaymodelplugin;

import me.aneleu.displaymodelplugin.commands.DisplayCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class DisplayModelPlugin extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Objects.requireNonNull(getCommand("r")).setExecutor(this);
        Objects.requireNonNull(getCommand("display")).setExecutor(new DisplayCommand(this));

    }

    @Override
    public void onDisable() {
        saveConfig();

        DisplayCommand.grabDisplay.values().forEach((display) -> {
            display.setGlowing(false);
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        Bukkit.broadcast(Component.text("RELOADING...", NamedTextColor.RED));
        Bukkit.reload();
        Bukkit.broadcast(Component.text("RELOAD COMPLETE.", NamedTextColor.AQUA));

        return true;
    }
}
