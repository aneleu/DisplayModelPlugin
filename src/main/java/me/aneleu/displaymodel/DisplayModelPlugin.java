package me.aneleu.displaymodel;

import me.aneleu.displaymodel.Commands.ModelCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DisplayModelPlugin extends JavaPlugin {

    public HashMap<String, DisplayModel> objects = new HashMap<>();

    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getCommand("model").setExecutor(new ModelCommand(this));

    }

}
