package com.popupmc.areaspawnlite.cache;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import com.popupmc.areaspawnlite.config.ConfigFile;
import org.bukkit.*;

// Will keep trying until it finds a single location
public class FindLocation {
    public FindLocation(AreaSpawnLite plugin, LocationFoundCB cb) {

        // Save plugin
        this.plugin = plugin;
        this.cb = cb;

        // Get easy ref to config
        config = plugin.settingFiles.configFile;

        // Start loop
        newLoop();
    }

    public void newLoop() {
        // Get random location
        Location loc = config.getRandomLocation();

        // Get the world it's in
        World world = loc.getWorld();

        // Ensure chunk is loaded, if not loaded then async load it first
        if(!world.isChunkLoaded(loc.getBlockX(), loc.getBlockZ()))
            loadWorld(world, loc);

        // Otherwise proceed to directly work with the chunk
        else
            worldIsLoaded(world, loc);
    }

    // Async loads the world first
    public void loadWorld(World world, Location loc) {
        world.getChunkAtAsync(loc.getBlockX(), loc.getBlockZ()).thenRun(() -> worldIsLoaded(world, loc));
    }

    // With the chunk loaded we can go ahead and directly work with it
    public void worldIsLoaded(World world, Location loc) {

        // Find highest block going no higher than 150 and no lower than 70
        for(int i = 256; i >= 70; i--) {
            // Check to see if it's air and skip if not (Keep moving down)
            Material block = world.getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).getType();
            if(block.isAir())
                continue;

            // If the highest block is bigger than 150, this location is a failure
            if(i > 150)
                break;

            // If it's not a good block then mark this location as a failure
            if(!config.blocks.materials.contains(block))
                break;

            // This location is a success, mark complete
            else {
                Location newLoc = loc.clone();
                newLoc.setY(i);
                locationSuccess(newLoc);
                return;
            }
        }

        locationFailure();
    }

    public void locationSuccess(Location loc) {
        cb.locationFoundCB(loc);
    }

    public void locationFailure() {
        newLoop();
    }

    public AreaSpawnLite plugin;
    public ConfigFile config;
    public LocationFoundCB cb;
}
