package com.artillexstudios.axcrates.crates.placedanimation;

import org.bukkit.Location;

public class TornadoAnimation extends Animation {

    public TornadoAnimation(Location location, String particle, boolean reverse) {
        super(50, location, particle, reverse);
    }

    protected void run() {
        final Location loc = location.clone();
        loc.add(0, -0.5 + (frame * 0.1), 0);

        location.getWorld().spawnParticle(particle, loc, 5 + frame, 0 + (frame * 0.01), 0, 0 + (frame * 0.01), 1, options);
    }
}
