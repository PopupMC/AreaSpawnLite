package com.popupmc.areaspawnlite;

import com.popupmc.areaspawnlite.cache.RebuildLocations;
import com.popupmc.areaspawnlite.commands.OnAslCommand;
import com.popupmc.areaspawnlite.commands.OnTravelCommand;
import com.popupmc.areaspawnlite.config.SettingFiles;
import com.popupmc.areaspawnlite.config.ConfigException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class AreaSpawnLite extends JavaPlugin {
    @Override
    public void onEnable() {
        // Load SettingFiles
        try {
            settingFiles = new SettingFiles(this);
        } catch (ConfigException e) {
            getLogger().warning("Unable to load setting files, disabling plugin...");
            setEnabled(false);
            return;
        }

        // Request a rebuild of locations on start
        RebuildLocations.requestRun(this);

        // Register /travel command executor
        Objects.requireNonNull(this.getCommand("travel")).setExecutor(new OnTravelCommand(this));
        Objects.requireNonNull(this.getCommand("asl")).setExecutor(new OnAslCommand(this));

        // Log enabled status
        getLogger().info("AreaSpawnLite is enabled.");
    }

    // Log disabled status
    @Override
    public void onDisable() {
        getLogger().info("AreaSpawnLite is disabled");
    }

    public SettingFiles settingFiles;
}
