package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramLine;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.StaticPlaceholder;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class PlacedCrate {
    private final Location location;
    private final Crate crate;
    private final Hologram hologram;

    public PlacedCrate(@NotNull Location location, @NotNull Crate crate) {
        this.location = location;
        this.crate = crate;

        final Location holoLoc = location.clone();
        holoLoc.add(0.5, 0.5, 0.5);
        holoLoc.add(crate.placedHologramOffsetX, crate.placedHologramOffsetY, crate.placedHologramOffsetZ);
        hologram = new Hologram(holoLoc, Serializers.LOCATION.serialize(location), crate.placedHologramLineHeight);
        hologram.addLines(StringUtils.formatListToString(crate.placedHologramLines), HologramLine.Type.TEXT);
        hologram.addPlaceholder(new StaticPlaceholder(string -> {
            return string.replace("%crate%", crate.displayName);
        }));
    }

    public void remove() {
        hologram.remove();
    }

    public Location getLocation() {
        return location;
    }

    public Crate getCrate() {
        return crate;
    }

    public Hologram getHologram() {
        return hologram;
    }
}
