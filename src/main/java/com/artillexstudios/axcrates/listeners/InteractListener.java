package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;

public class InteractListener implements Listener {
    public static final HashMap<Player, Consumer<Location>> selectionLocations = new HashMap<>();
    private final WeakHashMap<Player, Long> cooldowns = new WeakHashMap<>();

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;
        if (selectionLocations.containsKey(player)) {
            selectionLocations.remove(player).accept(event.getClickedBlock().getLocation());
            event.setCancelled(true);
            return;
        }

        for (Crate crate : CrateManager.getCrates().values()) {
            for (PlacedCrate placedCrate : crate.getPlacedCrates()) {
                if (!placedCrate.getLocation().equals(event.getClickedBlock().getLocation())) continue;
                if (cooldowns.containsKey(player) && System.currentTimeMillis() - cooldowns.get(player) < 100) {
                    event.setCancelled(true);
                    return;
                }
                cooldowns.put(player, System.currentTimeMillis());
                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    if (player.isSneaking()) return;
                    event.setCancelled(true);
                    placedCrate.openPreview(player);
                } else {
                    event.setCancelled(true);
                    final boolean multiOpen = CONFIG.getBoolean("multi-opening.enabled");
                    final int max = CONFIG.getInt("multi-opening.max");
                    crate.open(player, (player.isSneaking() && multiOpen) ? max : 1, false, false, placedCrate, null);
                }
                return;
            }
        }

        // todo: if holding lootbox, run
    }
}
