package com.artillexstudios.axcrates.crates;

import org.bukkit.Location;

public class PlacedCrate {
    private final Location location;
    private final Crate crate;
    private final CrateSettings crateSettings;

    public PlacedCrate(Location location, Crate crate, CrateSettings crateSettings) {
        this.location = location;
        this.crate = crate;
        this.crateSettings = crateSettings;
    }
}
