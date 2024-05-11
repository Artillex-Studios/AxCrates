package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateRewards;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Crate extends CrateSettings {
    public final String name;
    private final ArrayList<PlacedCrate> placedCrates = new ArrayList<>();
    private final CrateRewards crateRewards = new CrateRewards(settings);

    public Crate(Config settings, String name) {
        super(settings);
        this.name = name;
    }

    public ArrayList<PlacedCrate> getPlacedCrates() {
        return placedCrates;
    }

    public CrateRewards getCrateRewards() {
        return crateRewards;
    }

    public void open(Player player, int amount, boolean silent) {
        // don't check for requirements here
        // todo: knockback if no item / requirement fail
        // todo: silent

        // the legendary tripe for
        for (int i = 0; i < amount; i++) {
            for (List<CrateReward> rewards : crateRewards.rollAll().values()) {
                for (CrateReward reward : rewards) {
                    reward.run(player);
                }
            }
        }
    }

    public Config getSettings() {
        return settings;
    }

    public void reload() {
        refreshSettings();
        // todo: refresh placedCrates
        crateRewards.updateTiers();
    }
}
