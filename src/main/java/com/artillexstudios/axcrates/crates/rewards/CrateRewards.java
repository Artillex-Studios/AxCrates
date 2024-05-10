package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CrateRewards {
    protected final Config settings;
    protected final HashMap<String, CrateTier> tiers = new HashMap<>();

    public CrateRewards(Config settings) {
        this.settings = settings;
        updateTiers();
    }

    protected void updateTiers() {
        for (String str : settings.getSection("rewards").getRoutesAsStrings(false)) {
            tiers.put(str, new CrateTier(settings.getMapList("rewards." + str)));
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
