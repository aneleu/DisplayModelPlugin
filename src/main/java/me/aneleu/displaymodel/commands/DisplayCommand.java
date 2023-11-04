package me.aneleu.displaymodel.commands;

import me.aneleu.displaymodel.DisplayModelPlugin;
import me.aneleu.displaymodel.util.RaycastUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayCommand implements CommandExecutor {

    DisplayModelPlugin plugin;

    Player p;

    HashMap<String, BlockDisplay> grab_entity = new HashMap<>();

    public DisplayCommand() {
        plugin = DisplayModelPlugin.plugin;
    }


    private void sendInfoMessage(String message) {
        p.sendMessage(Component.text(message).color(NamedTextColor.GREEN));
    }

    private void sendSuggestionMessage(String message) {
        p.sendMessage(Component.text(message).color(NamedTextColor.GRAY));
    }

    private void sendErrorMessage(String message) {
        p.sendMessage(Component.text(message).color(NamedTextColor.RED));
    }

    
    // TODO 프로퍼티 설정마다 값을 입력 안하면 현재 정보를 볼 수 있게
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            p = (Player) sender;
        } else {
            return true;
        }
        
        if (args[0].equalsIgnoreCase("spawn")) {
            // spawn <태그> <블럭>
            Location loc = new Location(p.getWorld(), p.getX(), p.getY(), p.getZ());
            BlockDisplay blockDisplay = (BlockDisplay) p.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);
            if (args.length == 3) {
                blockDisplay.setBlock(Material.matchMaterial(args[2]).createBlockData());
            } else {
                blockDisplay.setBlock(p.getInventory().getItemInMainHand().getType().createBlockData());
            }
            blockDisplay.addScoreboardTag(args[1]);
            
        } else if (args[0].equalsIgnoreCase("grab")) {
            // grab
            List<BlockDisplay> raycast_result = new ArrayList<>();
            
            Location eye_loc = p.getEyeLocation();
            double[] point = {eye_loc.getX(), eye_loc.getY(), eye_loc.getZ()};
            double[] point2 = {eye_loc.getX() + p.getLocation().getDirection().getX(), eye_loc.getY() + p.getLocation().getDirection().getY(), eye_loc.getZ() + p.getLocation().getDirection().getZ()};

            for (BlockDisplay display: p.getWorld().getEntitiesByClass(BlockDisplay.class)) {
                Transformation trans = display.getTransformation();
                AxisAngle4d axis_angle = new AxisAngle4d();
                trans.getLeftRotation().get(axis_angle);

                double x = display.getX() + trans.getTranslation().x;
                double y = display.getY() + trans.getTranslation().y;
                double z = display.getZ() + trans.getTranslation().z;
                double dx = trans.getScale().x;
                double dy = trans.getScale().y;
                double dz = trans.getScale().z;
                double[] origin = {x, y, z};
                double[] axis = {axis_angle.x, axis_angle.y, axis_angle.z};
                double theta = -axis_angle.angle;
                double[] pos1 = {x, y, z};
                double[] pos2 = {x + dx, y, z};
                double[] pos3 = {x + dx, y, z + dz};
                double[] pos4 = {x, y, z + dz};
                double[] pos5 = {x, y + dy, z};
                double[] pos6 = {x + dx, y + dy, z};
                double[] pos7 = {x + dx, y + dy, z + dz};
                double[] pos8 = {x, y + dy, z + dz};
                double[][] cube = {pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8};
                
                // 축을 기준으로 회전시킨 두 점 구하기
                double[] dotA = RaycastUtil.rotateDotByVector(point, origin, axis, theta);
                double[] dotB = RaycastUtil.rotateDotByVector(point2, origin, axis, theta);
                
                // 레이캐스트 결과
                boolean result = RaycastUtil.intersects(cube, dotA, dotB);

                if (result) {
                    raycast_result.add(display);
                }

            }

            if (raycast_result.size() != 0) {
                double min_dis = raycast_result.get(0).getLocation().distance(p.getLocation());
                BlockDisplay min_e = raycast_result.get(0);
                for(BlockDisplay display: raycast_result) {
                    double distance = display.getLocation().distance(p.getLocation());
                    if (distance < min_dis) {
                        min_dis = distance;
                        min_e = display;
                    }
                }
                grab_entity.put(p.getName(), min_e);
                sendInfoMessage("디스플레이를 그랩했습니다.");
            } else {
                sendSuggestionMessage("바라보고 있는 곳에 디스플레이가 없습니다.");
            }
            
        } else if (args[0].equalsIgnoreCase("release")) {
            // release
            if (grab_entity.get(p.getName()) != null) {
                grab_entity.put(p.getName(), null);
                sendInfoMessage("디스플레이를 놓았습니다.");
            } else {
                sendSuggestionMessage("그랩하고 있는 디스플레이가 없습니다.");
            }

        } else if (args[0].equalsIgnoreCase("block")) {
            // block <블럭>
            if (grab_entity.get(p.getName()) == null) {
                sendErrorMessage("그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            if (args.length == 2) {
                grab_entity.get(p.getName()).setBlock(Material.matchMaterial(args[1]).createBlockData());
            } else {
                grab_entity.get(p.getName()).setBlock(p.getInventory().getItemInMainHand().getType().createBlockData());
            }

        } else if (args[0].equalsIgnoreCase("scale")) {
            // scale <x> <y> <z>

            BlockDisplay display = grab_entity.get(p.getName());
            if (display == null) {
                sendErrorMessage("그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            if (args.length == 1) {
                // TODO
            } else if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                Transformation tf = display.getTransformation();
                tf.getScale().set(x, y, z);
                display.setTransformation(tf);
            }

        } else if (args[0].equalsIgnoreCase("rotation")) {
            // rotation <x> <y> <z> <degree>

            BlockDisplay display = grab_entity.get(p.getName());
            if (display == null) {
                sendErrorMessage("그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            if (args.length == 1) {
                // TODO
            } else if (args.length == 5) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                double magnitude = Math.sqrt(x*x + y*y + z*z);
                if (magnitude != 0) {
                    x /= magnitude;
                    y /= magnitude;
                    z /= magnitude;
                }
                float angle = (float) (Float.parseFloat(args[4]) * 0.017453292519943295);
                Transformation tf = display.getTransformation();
                tf.getLeftRotation().setAngleAxis(angle, x, y, z);
                display.setTransformation(tf);
            }

        } else if (args[0].equalsIgnoreCase("location")) {
            // location <x> <y> <z>

            BlockDisplay display = grab_entity.get(p.getName());
            if (display == null) {
                sendErrorMessage("그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            if (args.length == 1) {
                // TODO
            } else if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                display.teleport(new Location(p.getWorld(), x, y, z));
            }

        } else if (args[0].equalsIgnoreCase("move")) {
            // move <dx> <dy> <dz>

            BlockDisplay display = grab_entity.get(p.getName());
            if (display == null) {
                sendErrorMessage("그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            if (args.length == 1) {
                // TODO
            } else if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                display.teleport(display.getLocation().add(x, y, z));
            }

        } else if (args[0].equalsIgnoreCase("translation")) {
            // translation <x> <y> <z>

            BlockDisplay display = grab_entity.get(p.getName());
            if (display == null) {
                sendErrorMessage("그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            if (args.length == 1) {
                // TODO
            } else if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                Transformation tf = display.getTransformation();
                tf.getTranslation().set(x, y, z);
                display.setTransformation(tf);
            }
            
        }

        return true;
    }
}
