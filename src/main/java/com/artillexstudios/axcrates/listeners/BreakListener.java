package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BreakListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBreak(@NotNull BlockBreakEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        for (Crate crate : CrateManager.getCrates().values()) {
            for (PlacedCrate placedCrate : crate.getPlacedCrates()) {
                if (!placedCrate.getLocation().equals(event.getBlock().getLocation())) continue;
                event.setCancelled(true);
                final List<Location> locations = new ArrayList<>(crate.placedLocations);
                locations.remove(placedCrate.getLocation());
                final ArrayList<String> locs = new ArrayList<>();
                for (Location location : locations) {
                    locs.add(Serializers.LOCATION.serialize(location));
                }
                crate.settings.set("placed.locations", locs);
                crate.reload();
                // todo: message, removed
                return;
            }
        }
    }
}
