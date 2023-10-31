package me.aneleu.displaymodel;

import org.bukkit.Location;

public class DisplayModel {

    String model_name;
    String obj_name;
    Location loc;

    public DisplayModel(String model_name, String obj_name, Location loc) {
        this.model_name = model_name;
        this.obj_name = obj_name;
        this.loc = loc;

    }

}
