package com.artillexstudios.axcrates.crates.placedanimation;

import org.bukkit.Location;

public class SpiralAnimation extends Animation {

    public SpiralAnimation(Location location, String particle, boolean reverse) {
        super(60, location, particle, reverse);
    }

    protected void run() {
        final Location loc = location.clone();
        double x, y;
        x = Math.cos(Math.PI * 2 * (frame * 12) / 360) / 1.25;
        y = Math.sin(Math.PI * 2 * (frame * 12) / 360) / 1.25;
        loc.add(x, -0.75 + (frame * 0.05), y);
        location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
    }
}
