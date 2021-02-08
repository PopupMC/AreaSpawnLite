package com.popupmc.areaspawnlite.commands;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import com.popupmc.areaspawnlite.RandomTravel;
import com.popupmc.areaspawnlite.cache.RebuildLocations;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class OnAslCommand implements CommandExecutor {
    public OnAslCommand(AreaSpawnLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!sender.hasPermission("asl.use") && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
            return false;
        }

        if(args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Missing argument");
            return false;
        }

        String arg = args[0];

        if(arg.equalsIgnoreCase("ongoing-travels"))
            sender.sendMessage(ChatColor.GOLD + "Ongoing Travels: " + ChatColor.YELLOW + RandomTravel.instances.size());
        else if(arg.equalsIgnoreCase("locations"))
            sender.sendMessage(ChatColor.GOLD + "Locations: " + ChatColor.YELLOW + plugin.settingFiles.locationFile.locs.size());
        else if(arg.equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.GOLD + "Reloading...");

            if(plugin.settingFiles.load()) {
                sender.sendMessage(ChatColor.GREEN + "Done!");
                RebuildLocations.requestRun(plugin);
            }
            else
                sender.sendMessage(ChatColor.RED + "Failed");
        }
        else if(arg.equalsIgnoreCase("save")) {
            sender.sendMessage(ChatColor.GOLD + "Saving...");

            if(plugin.settingFiles.locationFile.save()) {
                sender.sendMessage(ChatColor.GREEN + "Done!");
            }
            else
                sender.sendMessage(ChatColor.RED + "Failed");
        }
        else if(arg.equalsIgnoreCase("locs-rebuilding"))
            sender.sendMessage(ChatColor.GOLD + "Is Rebuilding: " + ChatColor.YELLOW + (RebuildLocations.instance != null));
        else if(arg.equalsIgnoreCase("locs-startagain"))
            sender.sendMessage(ChatColor.GOLD + "Flagged Start Again: " + ChatColor.YELLOW + RebuildLocations.startAgain);
        else if(arg.equalsIgnoreCase("add-loc") && (sender instanceof Player)) {
            Location loc = ((Player)sender).getLocation().clone();
            loc.setY(loc.getBlockY() - 1);
            plugin.settingFiles.locationFile.add(loc);
            sender.sendMessage(ChatColor.GREEN + "Location added as a normal location!");
        }
        else if(arg.equalsIgnoreCase("add-persistent-loc") && (sender instanceof Player)) {
            Location loc = ((Player)sender).getLocation().clone();
            loc.setY(loc.getBlockY() - 1);
            plugin.settingFiles.locationFile.add(loc, true);
            sender.sendMessage(ChatColor.GREEN + "Location added as a persistent location!");
        }
        else if(arg.equalsIgnoreCase("travel-w-command")) {

            // 0 = travel-w-command
            // 1 = player Name
            // 2... = args
            if(args.length < 3) {
                sender.sendMessage(ChatColor.RED + "ERROR: Not enough args");
                return false;
            }

            String playerName = args[1];
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                sender.sendMessage(ChatColor.RED + "ERROR: Player not found.");
                return false;
            }

            // Remove first 2 elements
            String[] commandElsToRun = Arrays.copyOfRange(args, 2, args.length);
            String commandToRun = String.join(" ", commandElsToRun);

            // Teleport player instantly with command
            RandomTravel.queueTraveler(player, plugin, true, commandToRun);
        }

        return true;
    }

    AreaSpawnLite plugin;
}
