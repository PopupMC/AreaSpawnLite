package com.popupmc.areaspawnlite;

import com.popupmc.areaspawnlite.cache.RebuildLocations;
import com.popupmc.areaspawnlite.config.LocationEntry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class RandomTravel {
    public RandomTravel(Player player, AreaSpawnLite plugin) {
        this.plugin = plugin;
        this.player = player;

        instances.add(this);

        // Start travel
        travel();
    }

    public void travel() {
        // Check to see if there are any locations generated, if not stop here and request re-generation
        if(plugin.settingFiles.locationFile.locs.size() <= 0) {
            player.sendMessage(ChatColor.GOLD + "Warning: there are no locations generated yet... Try again in a few minutes.");
            RebuildLocations.requestRun(plugin);
            selfDestroy();
            return;
        }

        // Send message early to give immidiate response, there will be various async delays in-between
        // We don't want a long delay for thep layer to get this message
        player.sendMessage(ChatColor.GOLD + "Preparing to teleport you in about 5 seconds, don't move...");

        // Get a random location
        LocationEntry location = plugin.settingFiles.locationFile.getRandom();

        // Make sure it's loaded
        loadWorld(location.world, location);
    }

    // Ensure chunk is loaded first
    public void loadWorld(World world, LocationEntry loc) {
        world.getChunkAtAsync(loc.x, loc.z).thenRun(() -> worldIsLoaded(loc));
    }

    // Check if it's still safe
    public void worldIsLoaded(LocationEntry loc) {
        // Get block type
        Material material = loc.world.getBlockAt(loc.x, loc.y, loc.z).getType();

        // Act accordignly whether the block is safe or not
        if(plugin.settingFiles.configFile.blocks.materials.contains(material))
            blockSafe(loc);
        else
            blockUnsafe(loc);
    }

    // The found location is safe
    public void blockSafe(LocationEntry loc) {

        // Remove after it's been used
        // Only do this if it's not marked as persistent
        if(!loc.persistent) {
            plugin.settingFiles.locationFile.locs.remove(loc);
            plugin.settingFiles.locationFile.save();
            RebuildLocations.requestRun(plugin);
        }

        // Do a teleport after 5 seconds
        teleport = new BukkitRunnable() {
            @Override
            public void run() {

                // Make sure player didn't move, teleport if did
                Location curLoc = player.getLocation();

                if(curLoc.getBlockX() == preTeleportLocation.getBlockX() &&
                    curLoc.getBlockY() == preTeleportLocation.getBlockY() &&
                    curLoc.getBlockZ() == preTeleportLocation.getBlockZ())
                    // Make sure the chunk is still loaded after 5 seconds
                    loc.world.getChunkAtAsync(loc.x, loc.z).thenRun(() -> doTeleport(loc));
                else {
                    player.sendMessage(ChatColor.RED + "You moved from your last location, aborting teleport");
                    selfDestroy();
                }
            }
        }.runTaskLater(plugin, 5 * 20);

        preTeleportLocation = player.getLocation().clone();
    }

    public void blockUnsafe(LocationEntry loc) {
        // Always remove unsafe locations and request a rebuild
        plugin.settingFiles.locationFile.locs.remove(loc);
        plugin.settingFiles.locationFile.save();
        RebuildLocations.requestRun(plugin);

        // Start again on next server tick
        restart = new BukkitRunnable() {
            @Override
            public void run() {
                travel();
            }
        }.runTaskLater(plugin, 1);
    }

    public void doTeleport(LocationEntry loc) {
        player.sendMessage(ChatColor.GREEN + "Teleporting...");
        player.teleport(new Location(loc.world, loc.x, loc.y, loc.z));
        selfDestroy();
    }

    public void selfDestroy() {
        instances.remove(this);
    }

    public static void queueTraveler(Player player, AreaSpawnLite plugin) {
        new RandomTravel(player, plugin);
    }

    AreaSpawnLite plugin;
    Player player;
    BukkitTask restart;
    BukkitTask teleport;

    Location preTeleportLocation;

    public static ArrayList<RandomTravel> instances = new ArrayList<>();
}
