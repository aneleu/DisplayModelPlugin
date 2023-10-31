package me.aneleu.displaymodel.Commands;

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
 model remove <모델명>                     모델을 삭제합니다. (중요! 모델이 삭제되면 그 모델의 객체가 모두 월드에서 삭제됩니다.)
 model list                                모델 리스트를 출력합니다.
 model list <모델명>                       해당 모델의 객체 리스트를 출력합니다.
*/

public class ModelCommand implements TabExecutor {

    DisplayModelPlugin plugin;

    Player p;

    public ModelCommand(DisplayModelPlugin plugin) {
        this.plugin = plugin;
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
                if (e instanceof BlockDisplay bd) {
                    // 블럭 코드 (str)
                    String block = bd.getBlock().getAsString();

                    // 상대위치
                    double dx = bd.getX() + bd.getTransformation().getTranslation().x - coord.get(0);
                    double dy = bd.getY() + bd.getTransformation().getTranslation().y - coord.get(1);
                    double dz = bd.getZ() + bd.getTransformation().getTranslation().z - coord.get(2);
                    double[] loc = {dx, dy, dz};

                    // 스케일
                    double scale_x = bd.getTransformation().getScale().x;
                    double scale_y = bd.getTransformation().getScale().y;
                    double scale_z = bd.getTransformation().getScale().z;
                    double[] scale = {scale_x, scale_y, scale_z};

                    // TODO rotation도 만들기
                    
                    // config 저장
                    plugin.getConfig().set("model.model." + name + "." + String.valueOf(index) + ".block", block);
                    plugin.getConfig().set("model.model." + name + "." + String.valueOf(index) + ".loc", loc);
                    plugin.getConfig().set("model.model." + name + "." + String.valueOf(index) + ".scale", scale);

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
                        plugin.getConfig().set("model.center."+args[1], new double[]{p.getX(), p.getY(), p.getZ()});
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
                            plugin.getConfig().set("model.center."+args[1], new double[]{x, y, z});
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

                    saveModel(args[1], args[2]);

                } else {
                    sendSuggestionMessage("/model save <모델명> <태그>");
                }

            } else if (args[0].equalsIgnoreCase("spawn")) {

                if (args.length == 3) {
                    String model_name = args[1];
                    String obj_name = args[2];
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



        return List.of();
    }
}
