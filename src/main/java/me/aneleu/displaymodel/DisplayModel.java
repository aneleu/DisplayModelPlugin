package me.aneleu.displaymodel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DisplayModel {

    DisplayModelPlugin plugin;

    String model_name;
    String obj_name;
    World world;
    Location center_loc;
    
    List<Entity> display = new ArrayList<>();

    public DisplayModel(String model_name, String obj_name, Location loc) {
        plugin = DisplayModelPlugin.plugin;

        this.model_name = model_name;
        this.obj_name = obj_name;
        center_loc = loc;
        center_loc.setPitch(0);
        center_loc.setYaw(0);
        world = loc.getWorld();

        spawnModel();

    }

    void spawnModel() {
        plugin.getConfig().set("model.obj."+obj_name+".model", model_name);
        plugin.getConfig().set("model.obj."+obj_name+".loc", center_loc);
        // TODO rotation 추가하기
        // TODO 블럭디스플레이가 아닌 텍스트/아이템 디스플레이
        // TODO NullException 처리
        Transformation trans = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(), new AxisAngle4f());

        for (String s: plugin.getConfig().getConfigurationSection("model.model."+model_name).getKeys(false)) {
            Material block = Material.matchMaterial(plugin.getConfig().getString("model.model." + model_name + "." + s + ".block"));
            List<Float> loc = plugin.getConfig().getFloatList("model.model."+model_name+"."+s+".loc");
            List<Float> scale = plugin.getConfig().getFloatList("model.model."+model_name+"."+s+".scale");

            BlockDisplay bd = (BlockDisplay) world.spawnEntity(center_loc, EntityType.BLOCK_DISPLAY);
            bd.setBlock(block.createBlockData());
            trans.getTranslation().x = loc.get(0);
            trans.getTranslation().y = loc.get(1);
            trans.getTranslation().z = loc.get(2);
            trans.getScale().x = scale.get(0);
            trans.getScale().y = scale.get(1);
            trans.getScale().z = scale.get(2);
            bd.setTransformation(trans);
            display.add(bd);

            List<String> UUIDs = plugin.getConfig().getStringList("model.UUID");
            UUIDs.add(bd.getUniqueId().toString());
            plugin.getConfig().set("model.UUID", UUIDs);
            
        }

    }

}
