package com.popupmc.areaspawnlite.config;

import com.popupmc.areaspawnlite.AreaSpawnLite;

public class SettingFiles {
    public SettingFiles(AreaSpawnLite plugin) throws ConfigException {
        // Save plugin
        this.plugin = plugin;

        // Load config files
        if(!this.load())
            throw new ConfigException();
    }

    public boolean load() {

        // Load Individual SettingFiles
        try {
            this.configFile = new ConfigFile(plugin);
            this.locationFile = new LocationFile(plugin);
        } catch (ConfigException e) {
            plugin.getLogger().warning("A configuration exception occured, aborting loading...");
            return false;
        }

        return true;
    }

    // SettingFiles Options
    public ConfigFile configFile;
    public LocationFile locationFile;
    public AreaSpawnLite plugin;
}
