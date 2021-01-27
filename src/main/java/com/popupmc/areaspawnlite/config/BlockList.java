package com.popupmc.areaspawnlite.config;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockList {
    public BlockList(List<String> materialStrs, AreaSpawnLite plugin) {

        this.plugin = plugin;

        for(String materialStr : materialStrs) {
            materialStr = materialStr.toUpperCase().replaceAll(" ", "_");

            try {
                Material material = Material.valueOf(materialStr);
                this.materials.add(material);
            }
            catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Material " + materialStr + " doesn't exist, skipping...");
            }
        }
    }

    public ArrayList<Material> materials = new ArrayList<>();
    public AreaSpawnLite plugin;
}
