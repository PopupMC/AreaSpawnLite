package com.popupmc.areaspawnlite.config;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import com.popupmc.areaspawnlite.misc.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class ConfigFile {

    public ConfigFile(AreaSpawnLite plugin) throws ConfigException {

        this.plugin = plugin;

        // Create if doesnt exist
        plugin.saveDefaultConfig();

        // Load main config file
        FileConfiguration configFile = plugin.getConfig();

        // Get World Name
        String worldName = configFile.getString("spawn-world", "main");
        if(worldName == null) {
            plugin.getLogger().warning("Unable to get spawn world name, unable to load");
            throw new ConfigException();
        }

        // Get World
        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            plugin.getLogger().warning("Spawn world is null (Was it renamed?), unable to load");
            throw new ConfigException();
        }

        // Save Spawn world
        spawnWorld = world;

        // Spawn Radius
        spawnRadius = Integer.parseInt(
                Objects.requireNonNull(
                        configFile.getString("spawn-radius", "25,000,000")).replaceAll(",", ""));

        // No-Spawn Radius
        noSpawnRadius = Integer.parseInt(
                Objects.requireNonNull(
                        configFile.getString("nospawn-radius", "24,999,999")).replaceAll(",", ""));

        // Blocks
        blocks = new BlockList(configFile.getStringList("blocks"), plugin);

        // Size of cache of locations to keep
        cacheSize = configFile.getInt("cache-size", 25);
    }

    // Gets a random spawnZone x & z location with y = spawnZone.y in spawnZone world
    // Takes into account noSpawnZone
    public Location getRandomLocation() {
        // Get Quadrant
        int side = RandomUtil.getRandomNumberInRange(1, 4);

        switch (side) {
            case 1:
                return getRandomTopLocation();
            case 2:
                return getRandomRightLocation();
            case 3:
                return getRandomBottomLocation();
            case 4:
                return getRandomLeftLocation();
        }

        return getRandomLeftLocation();
    }

    public Location getRandomTopLocation() {
       return getRandomLocation(
               -spawnRadius, // From X
               noSpawnRadius, // From Z
               spawnRadius, // To X
               spawnRadius // To Z
       );
    }

    public Location getRandomRightLocation() {
        return getRandomLocation(
                noSpawnRadius, // From X
                -spawnRadius, // From Z
                spawnRadius, // To X
                spawnRadius // To Z
        );
    }

    public Location getRandomBottomLocation() {
        return getRandomLocation(
                -spawnRadius, // From X
                -noSpawnRadius, // From Z
                spawnRadius, // To X
                -spawnRadius // To Z
        );
    }

    public Location getRandomLeftLocation() {
        return getRandomLocation(
                -noSpawnRadius, // From X
                -spawnRadius, // From Z
                -spawnRadius, // To X
                spawnRadius // To Z
        );
    }

    public Location getRandomLocation(int fromX, int fromZ, int toX, int toZ) {
        int x = RandomUtil.getRandomNumberInRange(fromX, toX);
        int z = RandomUtil.getRandomNumberInRange(fromZ, toZ);

        return new Location(spawnWorld, x, 0, z);
    }

    public AreaSpawnLite plugin;

    public World spawnWorld;
    public BlockList blocks;
    public int cacheSize;

    public int spawnRadius;
    public int noSpawnRadius;
}
