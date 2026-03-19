package com.artillexstudios.axcrates.animation.opening.impl;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.blacklist.PlayerBlacklistManager;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;

public class NoAnimation extends Animation {
    private final PlayerBlacklistManager blacklistManager = new PlayerBlacklistManager();

    public NoAnimation(Player player, Crate crate, Location location, boolean silent, boolean force) {
        super(player, 0, crate, location, silent, force);
    }

    @Override
    protected void end() {
        generateRewards();

        for (CrateTier tier : rewards.keySet()) {
            List<CrateReward> tierRewards = rewards.get(tier);
            for (CrateReward reward : tierRewards) {
                if (blacklistManager.isBlacklisted(player, crate.name, reward.getId())) {
                    if (!silent) {
                        MESSAGEUTILS.sendLang(player, "errors.reward-blacklisted");
                    }
                } else {
                    reward.run(player);
                }
            }
        }
    }
}


