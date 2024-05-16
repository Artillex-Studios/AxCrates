package com.artillexstudios.axcrates.crates.placedanimation;

import org.bukkit.Location;

public class SimpleAnimation extends Animation {

    public SimpleAnimation(Location location, String particle, boolean reverse) {
        super(1, location, particle, reverse);
    }

    protected void run() {
        location.getWorld().spawnParticle(particle, location, 5, 0.5, 0.5, 0.5, 1, options);
    }
}
