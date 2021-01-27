package com.popupmc.areaspawnlite.config;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import com.popupmc.areaspawnlite.misc.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationFile {
    public LocationFile(AreaSpawnLite plugin) throws ConfigException {

        this.plugin = plugin;

        if(!load())
            throw new ConfigException();
    }

    public boolean add(Location loc, boolean persistent) {
        // Check if the location exists, if it does a location is returned
        LocationEntry ret = exists(loc);

        // If there's no location returned then it's safe to add it meaning it doesn't
        // already exist
        if(ret == null) {
            // Add location to list
            locs.add(new LocationEntry(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld(), persistent));

            // Do a save
            save();
        }

        // Return whether or not it already existed by checking to see if a location was returned
        return ret == null;
    }

    public boolean add(Location loc) {
        return add(loc, false);
    }

    public boolean rem(Location loc) {
        // Check to see if exists, if it does a location is returned
        LocationEntry ret = exists(loc);

        // Only remove if it exists
        if(ret != null) {
            // Remove using returned location
            locs.remove(ret);

            // Save updated list
            save();
        }

        // Return if successful or not
        return ret != null;
    }

    public LocationEntry exists(Location loc2) {

        // Set return value to be null (Doesn't exist)
        LocationEntry ret = null;

        // Attempt to find it in the locations
        for(LocationEntry loc : locs) {

            // Look for a precise match
            if(loc.x == loc2.getBlockX() &&
                    loc.y == loc2.getBlockY() &&
                    loc.z == loc2.getBlockZ() &&
                    loc.world == loc2.getWorld()) {

                // When found save reference to precise match and stop looking
                ret = loc;
                break;
            }
        }

        // Return location if found or null
        return ret;
    }

    public void clear() {
        locs.clear();
    }

    public boolean save() {
        // Create new empty list
        ArrayList<String> locsStrList = new ArrayList<>();

        // Add in all the locations as strings
        for(LocationEntry loc : locs) {
            locsStrList.add(loc.world.getName() + ";" + loc.x + ";" + loc.y + ";" + loc.z + ";" + loc.persistent);
        }

        // Save list into YML file
        locationsConfig.set("locations", locsStrList);

        // Delete file, if it doesnt exist wont throw error
//        locationsConfigFile.delete();

        // Save YML file
        try {
            locationsConfig.save(locationsConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().warning("Unable to save locations.yml file");
            return false;
        }

        return true;
    }

    public boolean load() {
        // Get locations file
        locationsConfigFile = new File(plugin.getDataFolder(), "locations.yml");
        locationsConfig = new YamlConfiguration();

        // Save only if doesn't exist to prevent annoying warning, this is literally the dumbest functionality
        // By definition if I say don't replace then warning me that your not replacing is pretty silly and stupid
        // and causes me to have to write extra code to prevent that warning defeating the whole purpose of the option
        if(!locationsConfigFile.exists()) {
            plugin.saveResource("locations.yml", false);
        }

        // Load locations file, prepare for errors
        try {
            locationsConfig.load(locationsConfigFile);
        }
        catch (IOException | InvalidConfigurationException ex) {
            plugin.getLogger().warning("Unable to load locations.yml file");
            ex.printStackTrace();
            return false;
        }

        // Clear cached old locations
        // Useful for a reload, Otherwise a reload would add even more locations (doubling the list)
        clear();

        // Load list of locations
        List<String> locsStrList = locationsConfig.getStringList("locations");

        // Loop through each location
        for(String locStr : locsStrList) {

            // A location has this format
            // "world_name;x;y;z;persistent"
            // Split up the string into pieces
            String[] locStrPieces = locStr.split(";");

            // There have to be 5 pieces to each list item, if not then it was hand-edited incorrectly or a saving
            // error happened and we must notify of error and skip this entry
            if(locStrPieces.length != 5) {
                plugin.getLogger().warning("Location: " + locStr + " is missing 5 pieces, skipping entry");
                continue;
            }

            // Prepare to hold a single location entry
            LocationEntry loc;

            // Parsing from string is error prone, prepare to encounter errors
            try {
                // Parse the location from the string
                // Start with x, y, and z first (#1, #2, & #3)
                // Then the world name #0
                // Then persistent #4
                World entryWorld = Bukkit.getWorld(locStrPieces[0]);
                if(entryWorld == null) {
                    plugin.getLogger().warning("Location: " + locStr + " world is null, has it be renamed? Skipping...");
                    continue;
                }

                loc = new LocationEntry(Integer.parseInt(locStrPieces[1]),
                        Integer.parseInt(locStrPieces[2]),
                        Integer.parseInt(locStrPieces[3]),
                        entryWorld,
                        Boolean.parseBoolean(locStrPieces[4]));
            }
            // The location string is wrong which means it was badly hand edited or saved wrong
            // Notify of warning and skip
            catch (NumberFormatException ex) {
                plugin.getLogger().warning("Location: " + locStr + " coords can't be parsed, skipping entry.");
                continue;
            }

            // Add it to the list, if it already exists then warn and skip (Prevents duplication)
            if(!add(new Location(loc.world, loc.x, loc.y, loc.z), loc.persistent))
                plugin.getLogger().warning("Location: " + locStr + " already exists, skipping...");
        }

        return true;
    }

    public LocationEntry getRandom() {
        return locs.get(RandomUtil.getRandomNumberInRange(0, locs.size()));
    }

    // Plugin
    public AreaSpawnLite plugin;

    // List of locations
    public ArrayList<LocationEntry> locs = new ArrayList<>();

    // Location File
    public File locationsConfigFile;
    public FileConfiguration locationsConfig;
}
