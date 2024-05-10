package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcrates.utils.RandomUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateTier {
    protected int rollAmount;
    protected final HashMap<CrateReward, Double> rewards = new HashMap<>();

    public CrateTier(@NotNull List<Map<Object, Object>> section) {
        for (Map<Object, Object> str : section) {
            if (str.containsKey("roll-amount")) {
                rollAmount = (int) str.get("roll-amount");
                continue;
            }

            final List<String> commands = (List<String>) str.getOrDefault("commands", new ArrayList<>());
            final ArrayList<ItemStack> items = new ArrayList<>();
            double chance = (double) str.get("chance");

            for (Map<Object, Object> it : (List<Map<Object, Object>>) str.getOrDefault("items", new HashMap<>())) {
                items.add(new ItemBuilder(it).get());
            }

            rewards.put(new CrateReward(
                    commands, items, chance
            ), chance);
        }
    }

    public CrateReward roll() {
        return RandomUtils.randomValue(rewards);
    }

    public int getRollAmount() {
        return rollAmount;
    }

    public HashMap<CrateReward, Double> getRewards() {
        return rewards;
    }

    public void setRollAmount(int rollAmount) {
        this.rollAmount = rollAmount;
    }
}
