package com.popupmc.areaspawnlite.commands;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import com.popupmc.areaspawnlite.RandomTravel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

// Handles /travel command
public class OnTravelCommand implements CommandExecutor {
    public OnTravelCommand(AreaSpawnLite plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if(args.length == 0)
            return onTravelSelfCommand(sender);
        else if(args.length == 1)
            return onTravelOthersCommand(sender, args[0]);

        return false;
    }

    public boolean onTravelSelfCommand(@NotNull CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "How are you supposed to travel when your not a player?");
            return false;
        }

        Player player = (Player)sender;

        // Normal teleport with auto instant
        RandomTravel.queueTraveler(player, plugin);
        return true;
    }

    public boolean onTravelOthersCommand(@NotNull CommandSender sender, String name) {
        if(!sender.hasPermission("travel.others") && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to /travel others!");
            return false;
        }

        Player player = Bukkit.getPlayer(name);
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Player can't be found.");
            return false;
        }

        // Instant teleport of other player
        RandomTravel.queueTraveler(player, plugin, true);
        return true;
    }

    AreaSpawnLite plugin;
}
