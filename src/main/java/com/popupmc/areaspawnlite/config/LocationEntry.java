package com.popupmc.areaspawnlite.config;

import org.bukkit.World;

// Contains a single location entry
public class LocationEntry {
    public LocationEntry(int x, int y, int z, World world, boolean persistent) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.persistent = persistent;
    }

    public int x;
    public int y;
    public int z;
    public World world;
    public boolean persistent;
}
