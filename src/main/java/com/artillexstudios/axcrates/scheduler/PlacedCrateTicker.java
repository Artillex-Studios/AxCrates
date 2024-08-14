package com.artillexstudios.axcrates.scheduler;

import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlacedCrateTicker {
    private static ScheduledExecutorService service = null;

    public static void start() {
        if (service != null) service.shutdown();

        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
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
        if (service == null) return;
        service.shutdown();
    }
}
