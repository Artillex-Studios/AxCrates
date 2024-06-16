package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axapi.entity.impl.PacketItem;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class NoAnimation extends Animation {
    private final ArrayList<PacketItem> entities = new ArrayList<>();

    public NoAnimation(Player player, Crate crate, Location location) {
        super(player, 0, crate, location);
    }

    @Override
    protected void run() {
    }

    protected void end() {
        getRewards();
        for (CrateReward reward : finalRewards) {
            reward.run(player);
        }
    }
}
