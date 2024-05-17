package com.artillexstudios.axcrates.crates.placedanimation.impl;

import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.crates.placedanimation.Animation;
import org.bukkit.Location;

public class TornadoAnimation extends Animation {

    public TornadoAnimation(PlacedCrate placed) {
        super(50, placed);
    }

    protected void run() {
        final Location loc = location.clone();
        loc.add(0, -0.5 + (frame * 0.1), 0);

        location.getWorld().spawnParticle(particle, loc, 5 + frame, 0 + (frame * 0.01), 0, 0 + (frame * 0.01), 1, options);
    }
}
