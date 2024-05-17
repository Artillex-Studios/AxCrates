package com.artillexstudios.axcrates.scheduler;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.crates.openinganimation.Animation;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PlacedCrateTicker {
    private static ScheduledFuture<?> future = null;

    public static void start() {
        if (future != null) future.cancel(true);

        future = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                for (Crate crate : CrateManager.getCrates().values()) {
                    for (PlacedCrate placed : crate.getPlacedCrates()) {
                        placed.tick();
                    }
                }
                for (Animation animation : new ArrayList<>(Animation.animations)) {
                    animation.play();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

    public static void stop() {
        future.cancel(true);
    }
}
