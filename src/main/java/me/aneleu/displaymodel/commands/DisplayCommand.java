package me.aneleu.displaymodel.commands;

import me.aneleu.displaymodel.DisplayModelPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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
            // TODO 가까운 엔티티 선택에서 "레이트레이스"로 바꾸기!!!!!
            // TODO grab_entity에 플레이어별로 그랩한 엔티티 UUID저장하기

            double min_dis = 6;
            BlockDisplay min_e = null;
            for(Entity e: p.getNearbyEntities(3, 3, 3)) {
                if (e.getType() == EntityType.BLOCK_DISPLAY) {
                    double distance = e.getLocation().distance(p.getLocation());
                    if (distance < min_dis) {
                        min_dis = distance;
                        min_e = (BlockDisplay) e;
                    }
                }

            }

            if (min_e != null) {
                grab_entity.put(p.getName(), min_e);
                sendInfoMessage("엔티티를 그랩했습니다.");
            } else {
                sendSuggestionMessage("주변에 엔티티가 없습니다.");
            }
            
        } else if (args[0].equalsIgnoreCase("release")) {
            // release
            if (grab_entity.get(p.getName()) != null) {
                grab_entity.put(p.getName(), null);
                sendInfoMessage("엔티티를 놓았습니다.");
            } else {
                sendSuggestionMessage("그랩하고 있는 엔티티가 없습니다.");
            }

        } else if (args[0].equalsIgnoreCase("block")) {
            // block <블럭>
            if (args.length == 2) {
                grab_entity.get(p.getName()).setBlock(Material.matchMaterial(args[1]).createBlockData());
            } else {
                grab_entity.get(p.getName()).setBlock(p.getInventory().getItemInMainHand().getType().createBlockData());
            }

        } else if (args[0].equalsIgnoreCase("scale")) {
            // scale <x> <y> <z>

        } else if (args[0].equalsIgnoreCase("rotation")) {
            // rotation <x> <y> <z> <radian>

        } else if (args[0].equalsIgnoreCase("location")) {
            // location <x> <y> <z>

        } else if (args[0].equalsIgnoreCase("move")) {
            // move <dx> <dy> <dz>

        } else if (args[0].equalsIgnoreCase("translation")) {
            // translation <x> <y> <z>

        }

        return true;
    }
}
