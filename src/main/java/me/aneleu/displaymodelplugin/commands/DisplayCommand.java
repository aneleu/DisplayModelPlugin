package me.aneleu.displaymodelplugin.commands;

import me.aneleu.displaymodelplugin.DisplayModelPlugin;
import me.aneleu.displaymodelplugin.utils.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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

import java.util.HashMap;
import java.util.Set;

public class DisplayCommand implements CommandExecutor {

    final DisplayModelPlugin plugin;
    Player player;
    String playerName;

    public static HashMap<String, BlockDisplay> grabDisplay = new HashMap<>();

    public DisplayCommand(DisplayModelPlugin plugin) {
        this.plugin = plugin;
    }

    private static void grab(String playerName, BlockDisplay blockDisplay) {
        release(playerName);
        grabDisplay.put(playerName, blockDisplay);
        blockDisplay.setGlowing(true);
    }

    private static boolean release(String playerName) {
        if (isGrabbing(playerName)) {
            grabDisplay.get(playerName).setGlowing(false);
            grabDisplay.remove(playerName);
            return true;
        }
        return false;
    }

    private static boolean isGrabbing(String playerName) {
        if (!grabDisplay.containsKey(playerName)) {
            return false;
        }
        BlockDisplay display = grabDisplay.get(playerName);
        if (display.isDead()) {
            grabDisplay.remove(playerName);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (sender instanceof Player) {
            player = (Player) sender;
            playerName = player.getName();
        } else {
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            // spawn : 자신의 위치에 자신이 들고 있는 블럭의 디스플레이를 소환합니다.
            // spawn <블럭> : 자신의 위치에 <블럭> 블럭의 디스플레이를 소환합니다.
            // spawn this <태그> : 자신의 위치에 자신이 들고 있는 블럭의 디스플레이를 소환합니다. <태그> 태그가 붙습니다.
            // spawn <블럭> <태그> : 자신의 위치에 <블럭> 블럭의 디스플레이를 소환합니다. <태그> 태그가 붙습니다.
            // 참고) 디스플레이를 소환하면 자동으로 소환한 블럭이 그랩됩니다.
            Material material;
            if (args.length == 1 || args[1].equalsIgnoreCase("this")) {
                material = player.getInventory().getItemInMainHand().getType();
                if (!material.isBlock() || material.isAir()) {
                    Message.sendErrorMessage(player, "블럭을 들고 있지 않습니다.");
                    return true;
                }
            } else {
                material = Material.matchMaterial(args[1]);
                if (material == null) {
                    Message.sendErrorMessage(player, "존재하지 않는 블럭입니다.");
                    return true;
                } else if (!material.isBlock() || material.isAir()) {
                    Message.sendErrorMessage(player, "블럭이 아닙니다.");
                    return true;
                }
            }
            Location location = new Location(player.getWorld(), player.getX(), player.getY(), player.getZ());
            BlockDisplay blockDisplay = (BlockDisplay) player.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
            blockDisplay.setBlock(material.createBlockData());
            if (args.length == 3) {
                blockDisplay.addScoreboardTag(args[2]);
            }
            grab(playerName, blockDisplay);
            Message.sendInfoMessage(player, "디스플레이를 소환했습니다. 소환된 디스플레이가 그랩되었습니다.");

        } else if (args[0].equalsIgnoreCase("grab")) {
            // TODO




        } else if (args[0].equalsIgnoreCase("release")) {
            // release : 그랩한 블럭을 놓습니다.
            boolean result = release(playerName);
            if (result) {
                Message.sendInfoMessage(player, "디스플레이를 놓았습니다.");
            } else {
                Message.sendSuggestionMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
            }

        } else if (args[0].equalsIgnoreCase("block")) {
            // block [this] : 그랩된 디스플레이를 현재 들고 있는 블럭으로 변경합니다.
            // block <블럭> : 그랩된 디스플레이를 <블럭> 블럭으로 변경합니다.
            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            Material material;
            if (args.length == 1 || args[1].equalsIgnoreCase("this")) {
                material = player.getInventory().getItemInMainHand().getType();
                if (!material.isBlock() || material.isAir()) {
                    Message.sendErrorMessage(player, "블럭을 들고 있지 않습니다.");
                    return true;
                }
            } else {
                material = Material.matchMaterial(args[1]);
                if (material == null) {
                    Message.sendErrorMessage(player, "존재하지 않는 블럭입니다.");
                    return true;
                } else if (!material.isBlock() || material.isAir()) {
                    Message.sendErrorMessage(player, "블럭이 아닙니다.");
                    return true;
                }
            }
            grabDisplay.get(playerName).setBlock(material.createBlockData());


        } else if (args[0].equalsIgnoreCase("scale")) {
            // scale <x> <y> <z>

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                Transformation transformation = display.getTransformation();
                transformation.getScale().set(x, y, z);
                display.setTransformation(transformation);
            }

        } else if (args[0].equalsIgnoreCase("rotation")) {
            // rotation <x> <y> <z> <degree>

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            if (args.length == 5) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                float magnitude = (float) Math.sqrt(x * x + y * y + z * z);
                if (magnitude != 0) {
                    x /= magnitude;
                    y /= magnitude;
                    z /= magnitude;
                }
                float angle = (float) Math.toRadians(Float.parseFloat(args[4]));
                Transformation transformation = display.getTransformation();
                transformation.getLeftRotation().setAngleAxis(angle, x, y, z);
                display.setTransformation(transformation);
            }

        } else if (args[0].equalsIgnoreCase("location")) {
            // location <x> <y> <z> : (x, y, z) 로 이동합니다.
            // location : 플레이어의 좌표로 이동합니다.

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            if (args.length == 1) {
                display.teleport(player.getLocation());
            } else if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                display.teleport(new Location(player.getWorld(), x, y, z));
            }

        } else if (args[0].equalsIgnoreCase("move")) {
            // move <dx> <dy> <dz> : 현재 좌표로부터 (dx, dy, dz) 만큼 이동합니다.

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                display.teleport(display.getLocation().add(x, y, z));
            }

        } else if (args[0].equalsIgnoreCase("translation")) {
            // translation <x> <y> <z>

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            if (args.length == 4) {
                float x = Float.parseFloat(args[1]);
                float y = Float.parseFloat(args[2]);
                float z = Float.parseFloat(args[3]);
                Transformation transformation = display.getTransformation();
                transformation.getTranslation().set(x, y, z);
                display.setTransformation(transformation);
            }

        } else if (args[0].equalsIgnoreCase("delete")) {

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            display.remove();
        } else if (args[0].equalsIgnoreCase("tag")) {

            if (!isGrabbing(playerName)) {
                Message.sendErrorMessage(player, "그랩하고 있는 디스플레이가 없습니다.");
                return true;
            }
            BlockDisplay display = grabDisplay.get(playerName);
            if (args.length == 1 || args[1].equalsIgnoreCase("list")) {
                Set<String> tags = display.getScoreboardTags();
                if (!tags.isEmpty()) {
                    for (String tag : tags) {
                        Message.sendSuggestionMessage(player, tag);
                    }
                } else {
                    Message.sendSuggestionMessage(player, "그랩한 디스플레이는 아무 태그도 가지고 있지 않습니다.");
                }
            } else if (args[1].equalsIgnoreCase("add")) {
                display.addScoreboardTag(args[2]);
                Message.sendInfoMessage(player, "그랩한 디스플레이에 " + args[2] + " 태그를 추가했습니다.");
            } else if (args[1].equalsIgnoreCase("remove")) {
                if (display.removeScoreboardTag(args[2])) {
                    Message.sendInfoMessage(player, "그랩한 디스플레이에 " + args[2] + " 태그를 삭제했습니다.");
                } else {
                    Message.sendSuggestionMessage(player, "그랩한 디스플레이의 태그 목록에 " + args[2] + "가 없습니다.");
                }
            }
        }

        return true;

    }

}
