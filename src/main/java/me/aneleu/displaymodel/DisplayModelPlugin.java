package me.aneleu.displaymodel;

import me.aneleu.displaymodel.Commands.ModelCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class DisplayModelPlugin extends JavaPlugin {

    public static DisplayModelPlugin plugin;

    public HashMap<String, DisplayModel> objects = new HashMap<>();

    @Override
    public void onEnable() {

        plugin = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // 오브젝트 리로드
        for (String string_UUID: getConfig().getStringList("model.UUID")) {
            Entity entity = Bukkit.getEntity(UUID.fromString(string_UUID));
            if (entity != null) {
                entity.remove();
            }
        }
        getConfig().set("model.UUID", null);
        ConfigurationSection configurationSection = getConfig().getConfigurationSection("model.obj");
        if (configurationSection != null) {
            for (String s: getConfig().getConfigurationSection("model.obj").getKeys(false)) {
                String model_name = getConfig().getString("model.obj."+s+".model");
                Location obj_loc = getConfig().getLocation("model.obj."+s+".loc");
                objects.put(s, new DisplayModel(model_name, s, obj_loc));
            }
        }
        
        // 커맨드
        getCommand("model").setExecutor(new ModelCommand());
        getCommand("test").setExecutor(new ModelCommand());

    }

}
