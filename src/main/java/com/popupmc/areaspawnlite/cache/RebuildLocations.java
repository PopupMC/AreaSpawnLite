package com.popupmc.areaspawnlite.cache;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import com.popupmc.areaspawnlite.config.ConfigFile;
import com.popupmc.areaspawnlite.misc.AbstractAsyncLoop;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RebuildLocations extends AbstractAsyncLoop {
    public RebuildLocations(AreaSpawnLite plugin) {
        super(true, plugin.settingFiles.configFile.cacheSize - plugin.settingFiles.locationFile.locs.size(), 0, plugin);

        // If 0 or less then stop here
        if(counter <= 0)
            return;

        // Save config
        this.config = plugin.settingFiles.configFile;

        // Begin loop
        newLoop();
    }

    @Override
    public void newLoop() {
        // Basically get a new valid spawn location
        locFinder = new FindLocation(plugin, this::locFound);
    }

    // Location was found
    public void locFound(Location loc) {
        // Attempt to add it and if successful (not a duplicate)
        // proceed onward
        if(plugin.settingFiles.locationFile.add(loc))
            endLoop();

        // If it already existed then start a new loop (without counting as a list addition)
        else
            newLoop();
    }

    @Override
    public void selfDestroy() {
        // Nullify instance
        instance = null;

        // Restart on next server tick if requested to restart
        if(startAgain) {
            startAgain = false;
            startDelay = new BukkitRunnable() {
                @Override
                public void run() {
                    requestRun(plugin);
                }
            }.runTaskLater(plugin, 1);
        }
    }

    public static void requestRun(AreaSpawnLite plugin) {
        // Attempt to start new instance
        // If not already running then it's started and we're done
        if(doRun(plugin))
            return;

        // Otherwise mark to start after it's done
        startAgain = true;
    }

    public static boolean doRun(AreaSpawnLite plugin) {

        // Refuse if already going
        if(instance != null)
            return false;

        // Start instance
        instance = new RebuildLocations(plugin);
        startAgain = false;
        return true;
    }

    // Easy ref to config
    public ConfigFile config;
    public FindLocation locFinder;

    public static RebuildLocations instance = null;
    public static boolean startAgain = false;
    public static BukkitTask startDelay;
}
