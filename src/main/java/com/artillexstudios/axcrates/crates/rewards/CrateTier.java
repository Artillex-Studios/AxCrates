package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcrates.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CrateTier {
    protected final String name;
    protected int rollAmount;
    protected final LinkedHashMap<CrateReward, Double> rewards = new LinkedHashMap<>();

    public CrateTier(@NotNull LinkedList<Map<Object, Object>> section, String name) {
        this.name = name;
        reload(section);
    }

    public CrateReward roll() {
        return RandomUtils.randomValue(rewards); // todo: check if rewards empty
    }

    public int getRollAmount() {
        return rollAmount;
    }

    public LinkedHashMap<CrateReward, Double> getRewards() {
        return rewards;
    }

    public void setRollAmount(int rollAmount) {
        this.rollAmount = rollAmount;
    }

    public String getName() {
        return name;
    }

    public void reload(@NotNull LinkedList<Map<Object, Object>> section) {
        rewards.clear();
        for (Map<Object, Object> str : section) {
            if (str.containsKey("roll-amount")) {
                rollAmount = (int) str.get("roll-amount");
                continue;
            }

            final List<String> commands = (List<String>) str.get("commands");
            final ArrayList<ItemStack> items = new ArrayList<>();
            double chance = (double) str.get("chance");

            var map = (List<Map<Object, Object>>) str.get("items");
            if (map != null) {
                final LinkedList<Map<Object, Object>> map2 = new LinkedList<>(map);
                for (Map<Object, Object> it : map2) {
                    items.add(new ItemBuilder(it).get());
                }
            }

            final ItemStack display;
            if (str.containsKey("display"))
                display = new ItemBuilder((Map<Object, Object>) str.get("display")).get();
            else
                display = new ItemStack(Material.RED_BANNER);

            rewards.put(new CrateReward(
                    commands == null ? new ArrayList<>() : commands, items, chance, display
            ), chance);
        }
    }
}
