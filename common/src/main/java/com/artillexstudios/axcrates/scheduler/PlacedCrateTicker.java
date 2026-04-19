package com.artillexstudios.axcrates.scheduler;

import com.artillexstudios.axapi.executor.ExceptionReportingScheduledThreadPool;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlacedCrateTicker {
    private static ScheduledExecutorService service = null;

    public static void start() {
        if (service != null) service.shutdown();

        service = new ExceptionReportingScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {
            try {
                for (Crate crate : CrateManager.getCrates().values()) {
                    for (PlacedCrate placed : crate.getPlacedCrates()) {
                        placed.tick();
                    }
                }

                List<Animation> endedAnimations = new ArrayList<>();
                for (Animation animation : Animation.animations) {
                    boolean ended = animation.play();
                    if (ended) endedAnimations.add(animation);
                }
                Animation.animations.removeAll(endedAnimations);
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
