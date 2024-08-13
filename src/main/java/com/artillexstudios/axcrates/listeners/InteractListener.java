package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.PlacedCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
    private static final WeakHashMap<Player, Long> cooldowns = new WeakHashMap<>();

    @EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
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
                if (interact(player, placedCrate, event.getAction())) event.setCancelled(true);
                return;
            }
        }

        // todo: if holding lootbox, run
    }

    public static boolean interact(Player player, PlacedCrate placedCrate, Action action) {
        if (cooldowns.containsKey(player) && System.currentTimeMillis() - cooldowns.get(player) < 100) {
            return true;
        }
        cooldowns.put(player, System.currentTimeMillis());
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (player.isSneaking()) return false;
            placedCrate.openPreview(player);
        } else {
            final boolean multiOpen = CONFIG.getBoolean("multi-opening.enabled");
            final int max = CONFIG.getInt("multi-opening.max");
            placedCrate.getCrate().open(player, (player.isSneaking() && multiOpen) ? max : 1, false, false, placedCrate, null);
        }
        return true;
    }
}
