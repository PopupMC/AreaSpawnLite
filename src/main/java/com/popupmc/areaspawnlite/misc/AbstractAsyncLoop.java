package com.popupmc.areaspawnlite.misc;

import com.popupmc.areaspawnlite.AreaSpawnLite;

// An async loop
public abstract class AbstractAsyncLoop {

    // Base setup
    public AbstractAsyncLoop(boolean countDown, int counter, int counterEnd, AreaSpawnLite plugin) {
        this.plugin = plugin;
        this.countDown = countDown;
        this.counter = counter;
        this.counterEnd = counterEnd;
    }

    // The start of a new loop
    // Intended to override, otherwise does nothing and calls loop end
    public void newLoop() {
        endLoop();
    }

    // The end of a loop, does work to determine if the loop needs to continue
    public void endLoop() {

        // Increment or Decrement counter
        if(countDown)
            counter--;
        else
            counter++;

        // Determine if needed to self-destroy
        if(countDown && (counter <= counterEnd)) {
            selfDestroy();
            return;
        }
        else if(!countDown && (counter >= counterEnd)) {
            selfDestroy();
            return;
        }

        // Begin new loop
        newLoop();
    }

    // Intended to self-destroy the loop, be the final loop end
    public void selfDestroy() {

    }

    public AreaSpawnLite plugin;

    public boolean countDown;
    public int counter;
    public int counterEnd;
}
