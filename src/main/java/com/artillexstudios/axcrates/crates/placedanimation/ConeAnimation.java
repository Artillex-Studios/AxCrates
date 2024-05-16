package com.artillexstudios.axcrates.crates.placedanimation;

import org.bukkit.Location;

public class ConeAnimation extends Animation {

    public ConeAnimation(Location location, String particle, boolean reverse) {
        super(70, location, particle, reverse);
    }

    protected void run() {
        for (int i = 0; i < 8; i++) {
            final Location loc = location.clone();
            double x, y;
            x = Math.cos(Math.PI * 2 * (frame * 12 + (45 * i)) / 360) / (1.0 + (frame * 0.03));
            y = Math.sin(Math.PI * 2 * (frame * 12 + (45 * i)) / 360) / (1.0 + (frame * 0.03));
            loc.add(x, -0.75 + (frame * 0.05), y);
            location.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 1, options);
        }
    }
}
