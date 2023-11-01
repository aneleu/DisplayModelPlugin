package me.aneleu.displaymodel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class DisplayModel {

    DisplayModelPlugin plugin;

    String model_name;
    String obj_name;
    World world;
    Location center_loc;
    double center_x;
    double center_y;
    double center_z;
    
    List<Entity> display = new ArrayList<>();

    public DisplayModel(String model_name, String obj_name, Location loc) {
        plugin = DisplayModelPlugin.plugin;

        this.model_name = model_name;
        this.obj_name = obj_name;
        center_loc = loc;
        center_loc.setPitch(0);
        center_loc.setYaw(0);
        center_x = center_loc.x();
        center_y = center_loc.y();
        center_z = center_loc.z();
        world = loc.getWorld();

        spawnModel();

    }

    void spawnModel() {
        plugin.getConfig().set("model.obj."+obj_name+".model", model_name);
        plugin.getConfig().set("model.obj."+obj_name+".loc", center_loc);
        // TODO NullException 처리
        Transformation trans = new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(), new AxisAngle4f());

        for (String s: plugin.getConfig().getConfigurationSection("model.model."+model_name).getKeys(false)) {
            Material block = Material.matchMaterial(plugin.getConfig().getString("model.model." + model_name + "." + s + ".block"));
            List<Double> location = plugin.getConfig().getDoubleList("model.model."+model_name+"."+s+".location");
            List<Float> translation = plugin.getConfig().getFloatList("model.model."+model_name+"."+s+".translation");
            List<Float> scale = plugin.getConfig().getFloatList("model.model."+model_name+"."+s+".scale");
            List<Float> left_rotation = plugin.getConfig().getFloatList("model.model."+model_name+"."+s+".rotation_quaternium");

            Location loc = center_loc.clone();
            loc.add(location.get(0), location.get(1), location.get(2));

            trans.getTranslation().x = translation.get(0);
            trans.getTranslation().y = translation.get(1);
            trans.getTranslation().z = translation.get(2);
            trans.getScale().x = scale.get(0);
            trans.getScale().y = scale.get(1);
            trans.getScale().z = scale.get(2);
            trans.getLeftRotation().w = left_rotation.get(0);
            trans.getLeftRotation().x = left_rotation.get(1);
            trans.getLeftRotation().y = left_rotation.get(2);
            trans.getLeftRotation().z = left_rotation.get(3);

            BlockDisplay bd = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY);
            bd.setBlock(block.createBlockData());
            bd.setTransformation(trans);
            display.add(bd);

            List<String> UUIDs = plugin.getConfig().getStringList("model.UUID");
            UUIDs.add(bd.getUniqueId().toString());
            plugin.getConfig().set("model.UUID", UUIDs);
            
        }

    }

}
