package com.popupmc.areaspawnlite.misc;

import com.popupmc.areaspawnlite.AreaSpawnLite;
import org.bukkit.World;

import java.util.logging.Level;

public abstract class AbstractAsyncChunkLoop extends AbstractAsyncLoop {
    public AbstractAsyncChunkLoop(boolean countDown, int counter, int counterEnd, AreaSpawnLite plugin) {
        super(countDown, counter, counterEnd, plugin);
    }

    // Process chunk, async loads it first if not already loaded
    public void processChunk(World world, int x, int z) {
        // Skip if the world is invalid (Such as being renamed)
        if(world == null) {
            plugin.getLogger().log(Level.WARNING, "World is null, skipping...");
            endLoop();
            return;
        }

        // Ensure chunk is loaded, if not async load it first
        if(!world.isChunkLoaded(x, z))
            loadChunk(world, x, z);

        // Otherwise proceed to directly work with the chunk
        else
            chunkIsLoaded(world, x, z);
    }

    // Async loads the chunk first
    public void loadChunk(World world, int x, int z) {
        world.getChunkAtAsync(x, z).thenRun(() -> chunkIsLoaded(world, x, z));
    }

    // With the chunk loaded we can go ahead and directly work with it
    public void chunkIsLoaded(World world, int x, int z) {
        endLoop();
    }
}
