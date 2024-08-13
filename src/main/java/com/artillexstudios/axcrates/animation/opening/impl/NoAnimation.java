package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class NoAnimation extends Animation {

    public NoAnimation(Player player, Crate crate, Location location) {
        super(player, 0, crate, location);
    }

    protected void end() {
        getRewards();
        for (CrateReward reward : finalRewards) {
            reward.run(player);
        }
    }
}
