package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.crates.openinganimation.impl.CircleAnimation;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateRewards;
import com.artillexstudios.axcrates.keys.KeyManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;

public class Crate extends CrateSettings {
    public final String name;
    private final ArrayList<PlacedCrate> placedCrates = new ArrayList<>();
    private final CrateRewards crateRewards = new CrateRewards(settings);

    public Crate(Config settings, String name) {
        super(settings);
        this.name = name;
        reloadPlaced();
    }

    public ArrayList<PlacedCrate> getPlacedCrates() {
        return placedCrates;
    }

    public CrateRewards getCrateRewards() {
        return crateRewards;
    }

    public void open(Player player, int amount, boolean silent, boolean force, @Nullable PlacedCrate placed, @Nullable Location loc) {
        if (!force) { // todo: check if inventory full
            // todo: check for requirements here
            var keyItems = KeyManager.hasKey(player, this);
            if (keyItems == null) {
                // todo: no keys message
                // todo: knockback if no item / requirement fail
                if (placed != null && placedKnockback) {
                    final Location location = placed.getLocation().clone();
                    location.add(0.5, 0, 0.5);
                    final Vector diff = location.toVector().subtract(player.getLocation().toVector());
                    diff.subtract(diff.clone().multiply(2));
                    player.setVelocity(diff.normalize()
                            .multiply(CONFIG.getFloat("knockback-strength.forwards"))
                            .setY(CONFIG.getFloat("knockback-strength.upwards")));
                    player.setFallDistance(0);
                }
                return;
            }

            int newAmount = amount;
            for (ItemStack it : keyItems) {
                if (it.getAmount() >= newAmount) {
                    it.setAmount(it.getAmount() - newAmount);
                    newAmount = 0;
                } else {
                    newAmount -= it.getAmount();
                    it.setAmount(0);
                }
                if (newAmount == 0) break;
            }

            if (newAmount != 0)
                amount = amount - newAmount;
        }
        // todo: silent

        // the legendary tripe for
        for (int i = 0; i < amount; i++) {
            for (List<CrateReward> rewards : crateRewards.rollAll().values()) {
                if (openAnimation.isBlank() || amount > 1 || (placed == null && loc == null)) {
                    for (CrateReward reward : rewards) {
                        reward.run(player);
                    }
                } else {
                    Location l = loc;
                    if (l == null) l = placed.getLocation();
                    switch (openAnimation) {
                        case "circle" -> {
                            new CircleAnimation(rewards, this, l);
                        }
                    }
                }
            }
        }
    }

    public Config getSettings() {
        return settings;
    }

    public void reload() {
        refreshSettings();
        crateRewards.updateTiers();
        reloadPlaced();
    }

    public void reloadPlaced() {
        for (PlacedCrate crate : placedCrates) {
            crate.remove();
        }
        placedCrates.clear();
        for (Location location : placedLocations) {
            placedCrates.add(new PlacedCrate(location, this));
        }
    }

    public void remove() {
        for (PlacedCrate crate : placedCrates) {
            crate.remove();
        }
    }
}
