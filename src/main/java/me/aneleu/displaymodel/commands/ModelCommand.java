package me.aneleu.displaymodel.commands;

import me.aneleu.displaymodel.DisplayModel;
import me.aneleu.displaymodel.DisplayModelPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;

import java.util.ArrayList;
import java.util.List;

/*
 명령어
 model create <모델명>                     모델을 생성합니다.
 model center <모델명>                     모델을 저장하고 불러올 때 중심이 되는 좌표를 현재 플레이어 좌표로 설정합니다.
 model center <모델명> <x> <y> <z>         모델을 저장하고 불러올 때 중심이 되는 좌표를 지정합니다.
 model save <모델명> <tag>                 모델을 저장합니다. 중심 좌표가 설정되지 않았을경우 플레이어의 현재 좌표를 기준으로 저장합니다.
 model spawn <모델명> <객체명>             모델을 현재 플레이어 좌표에 불러옵니다.
 model spawn <모델명> <객체명> <x> <y> <z> 모델을 지정한 좌표에 불러옵니다.
 model tp <객체명>                         모델을 현재 플레이어 위치로 텔레포트시킵니다.
 model tp <객체명> <x> <y> <z>             모델을 지정한 좌표로 텔레포트시킵니다.
 model kill <객체명>                       객체를 월드에서 삭제합니다.
 model remove <모델명>                     모델을 삭제합니다. (중요! 모델이 삭제되어도 그 모델의 오브젝트는 월드에서 제거되지 않습니다.)
 model list                                모델 리스트를 출력합니다.
 model list <모델명>                       해당 모델의 객체 리스트를 출력합니다.
*/

public class ModelCommand implements TabExecutor {

    DisplayModelPlugin plugin;

    Player p;

    public ModelCommand() {
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

    private void saveModel(String name, String tag) {
        World w = p.getWorld();

        // 중심 좌표 불러오기. 지정된 좌표가 없으면 플레이어의 좌표를 임시로 사용
        List<Double> coord = plugin.getConfig().getDoubleList("model.center."+name);
        if (coord.isEmpty()) {
            coord = new ArrayList<>(List.of(p.getX(), p.getY(), p.getZ()));
        }

        plugin.getConfig().set("model.model."+name, null);

        int index = 0;
        for (Entity e: w.getEntities()) {
            if (e.getScoreboardTags().contains(tag)) {
                // TODO 블럭디스플레이가 아닌 텍스트/아이템 디스플레이
                // TODO config에서 model.model.<모델명>.<n>.type: block / item / text
                if (e instanceof BlockDisplay bd) {
                    // 블럭 코드 (str)
                    String block = bd.getBlock().getAsString(true);

                    // location
                    double dx = bd.getX() - coord.get(0);
                    double dy = bd.getY() - coord.get(1);
                    double dz = bd.getZ() - coord.get(2);
                    List<Double> location = new ArrayList<>(List.of(dx, dy, dz));

                    // translation
                    float translation_x =  bd.getTransformation().getTranslation().x;
                    float translation_y = bd.getTransformation().getTranslation().y;
                    float translation_z = bd.getTransformation().getTranslation().z;
                    List<Float> translation = new ArrayList<>(List.of(translation_x, translation_y, translation_z));

                    // scale
                    float scale_x = bd.getTransformation().getScale().x;
                    float scale_y = bd.getTransformation().getScale().y;
                    float scale_z = bd.getTransformation().getScale().z;
                    List<Float> scale = new ArrayList<>(List.of(scale_x, scale_y, scale_z));

                    // left_rotation - quaternium
                    float quaternium_w = bd.getTransformation().getLeftRotation().w;
                    float quaternium_x = bd.getTransformation().getLeftRotation().x;
                    float quaternium_y = bd.getTransformation().getLeftRotation().y;
                    float quaternium_z = bd.getTransformation().getLeftRotation().z;
                    List<Float> left_rotation_q = new ArrayList<>(List.of(quaternium_w, quaternium_x, quaternium_y, quaternium_z));

                    // left_rotation - axis/angle
                    AxisAngle4d axis_angle = new AxisAngle4d();
                    bd.getTransformation().getLeftRotation().get(axis_angle);
                    double axis_x = axis_angle.x;
                    double axis_y = axis_angle.y;
                    double axis_z = axis_angle.z;
                    double angle = axis_angle.angle;
                    List<Double> left_rotation_aa = new ArrayList<>(List.of(axis_x, axis_y, axis_z, angle));

                    // yaw & pitch
                    float yaw = bd.getYaw();
                    float pitch = bd.getPitch();
                    List<Float> yaw_pitch = new ArrayList<>(List.of(yaw, pitch));

                    // config 저장
                    plugin.getConfig().set("model.model." + name + "." + index + ".block", block);
                    plugin.getConfig().set("model.model." + name + "." + index + ".location", location);
                    plugin.getConfig().set("model.model." + name + "." + index + ".translation", translation);
                    plugin.getConfig().set("model.model." + name + "." + index + ".scale", scale);
                    plugin.getConfig().set("model.model." + name + "." + index + ".rotation_quaternium", left_rotation_q);
                    plugin.getConfig().set("model.model." + name + "." + index + ".rotation_axis_angle", left_rotation_aa);
                    plugin.getConfig().set("model.model." + name + "." + index + ".yaw_pitch", yaw_pitch);

                }

            }
            index++;
        }

    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {

            p = (Player) sender;

            if (args.length == 0) {
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {

                if (args.length == 1) {
                    sendSuggestionMessage("/model create <모델명>");
                } else {
                    String name = args[1];
                    List<String> modelList = plugin.getConfig().getStringList("model.list");
                    if (modelList.contains(name)) {
                        sendErrorMessage("이미 해당 이름으로 된 모델이 존재합니다.");
                    } else {
                        modelList.add(name);
                        plugin.getConfig().set("model.list", modelList);
                        sendInfoMessage(args[1] + " 모델을 생성하였습니다.");
                    }

                }

            } else if (args[0].equalsIgnoreCase("center")) {

                if (args.length == 2) {
                    // model center <모델명>
                    if (plugin.getConfig().getStringList("model.list").contains(args[1])) {
                        List<Double> center = new ArrayList<>(List.of(p.getX(), p.getY(), p.getZ()));
                        plugin.getConfig().set("model.center."+args[1], center);
                        sendInfoMessage(args[1] + " 모델의 중심 좌표를 이 곳으로 설정했습니다.");
                    } else {
                        sendErrorMessage("존재하지 않는 모델입니다.");
                    }
                } else if (args.length == 5) {
                    // model center <모델명> <x> <y> <Z>
                    if (plugin.getConfig().getStringList("model.list").contains(args[1])) {
                        try {
                            double x = Double.parseDouble(args[2]);
                            double y = Double.parseDouble(args[3]);
                            double z = Double.parseDouble(args[4]);
                            List<Double> center = new ArrayList<>(List.of(x, y, z));
                            plugin.getConfig().set("model.center."+args[1], center);
                            sendInfoMessage(args[1] + " 모델의 중심 좌표를 (" + args[2] + ", " + args[3] + ", " + args[4] + ") 로 설정했습니다.");
                        } catch (Exception exception) {
                            sendErrorMessage("존재하지 않는 좌표입니다.");
                        }
                    } else {
                        sendErrorMessage("존재하지 않는 모델입니다.");
                    }
                } else {
                    sendSuggestionMessage("/model center <모델명>");
                    sendSuggestionMessage("/model center <모델명> <x> <y> <z>");
                }

            } else if (args[0].equalsIgnoreCase("save")) {

                if (args.length == 3) {

                    if (plugin.getConfig().getStringList("model.list").contains(args[1])) {
                        saveModel(args[1], args[2]);
                        sendInfoMessage("모델을 저장했습니다.");
                    } else {
                        sendErrorMessage("존재하지 않는 모델입니다.");
                    }

                } else {
                    sendSuggestionMessage("/model save <모델명> <태그>");
                }

            } else if (args[0].equalsIgnoreCase("spawn")) {

                if (args.length == 3) {
                    String model_name = args[1];
                    String obj_name = args[2];
                    // TODO 존재하지 않는 모델을 소환한 경우 오류 메시지 출력
                    plugin.objects.put(obj_name, new DisplayModel(model_name, obj_name, p.getLocation()));

                } else if (args.length == 6) {
                    // TODO 좌표를 지정한 경우
                } else {
                    sendSuggestionMessage("/model spawn <모델명> <객체명>");
                    sendSuggestionMessage("/model spawn <모델명> <객체명> <x> <y> <z>");
                }

            }

            plugin.saveConfig();

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // TODO

        return List.of();
    }
}
