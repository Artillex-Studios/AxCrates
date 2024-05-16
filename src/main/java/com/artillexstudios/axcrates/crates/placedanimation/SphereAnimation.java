package com.artillexstudios.axcrates.crates.placedanimation;

import org.bukkit.Location;

public class SphereAnimation extends Animation {

    public SphereAnimation(Location location, String particle, boolean reverse) {
        super(50, location, particle, reverse);
    }

    protected void run() {
        final Location loc = location.clone();
        location.getWorld().spawnParticle(particle, loc, frame, 0.015 * frame, 0.015 * frame, 0.015 * frame, 1, options);
    }
}
