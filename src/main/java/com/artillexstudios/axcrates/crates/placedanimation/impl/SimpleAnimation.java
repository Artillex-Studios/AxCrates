package com.artillexstudios.axcrates.crates.placedanimation.impl;

import com.artillexstudios.axcrates.crates.PlacedCrate;
import com.artillexstudios.axcrates.crates.placedanimation.Animation;

public class SimpleAnimation extends Animation {

    public SimpleAnimation(PlacedCrate placed) {
        super(1, placed);
    }

    protected void run() {
        location.getWorld().spawnParticle(particle, location, 5, 0.5, 0.5, 0.5, 1, options);
    }
}
