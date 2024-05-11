package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class CrateRewards {
    protected final Config settings;
    protected final LinkedHashMap<String, CrateTier> tiers = new LinkedHashMap<>();

    public CrateRewards(Config settings) {
        this.settings = settings;
        updateTiers();
    }

    public void updateTiers() {
        final HashSet<String> loadedTiers = new HashSet<>();
        for (String str : settings.getSection("rewards").getRoutesAsStrings(false)) {
            loadedTiers.add(str);
            if (tiers.containsKey(str)) {
                tiers.get(str).reload(new LinkedList<>(settings.getMapList("rewards." + str)));
                continue;
            }
            tiers.put(str, new CrateTier(new LinkedList<>(settings.getMapList("rewards." + str)), str));
        }

        for (String str : tiers.keySet()) {
            if (loadedTiers.contains(str)) continue;
            tiers.remove(str);
        }
    }

    public HashMap<String, CrateTier> getTiers() {
        return tiers;
    }

    public HashMap<CrateTier, List<CrateReward>> rollAll() {
        final HashMap<CrateTier, List<CrateReward>> map = new HashMap<>();

        for (CrateTier tier : tiers.values()) {
            List<CrateReward> rewards = new ArrayList<>();
            for (int i = 0; i < tier.rollAmount; i++) {
                rewards.add(tier.roll());
            }
            map.put(tier, rewards);
        }

        return map;
    }
}
