package com.artillexstudios.axcrates.animation.opening;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    public static ArrayList<Animation> animations = new ArrayList<>();
    protected final Crate crate;
    protected final Location location;
    protected int frame = 0;
    protected int totalFrames;
    protected List<CrateReward> finalRewards;
    protected final Player player;

    public Animation(Player player, int totalFrames, Crate crate, Location location) {
        this.player = player;
        this.totalFrames = totalFrames;
        this.crate = crate;
        this.location = location.clone().add(0.5, 0.5, 0.5);
        animations.add(this);
    }

    public void play() {
        frame++;
        if (frame >= totalFrames) {
            animations.remove(this);
            end();
            return;
        }
        run();
    }

    protected List<CrateReward> getRewards() {
        final List<CrateReward> rewards = new ArrayList<>();
        for (List<CrateReward> value : crate.getCrateRewards().rollAll().values()) {
            rewards.addAll(value);
        }
        finalRewards = rewards;
        return rewards;
    }

    protected void run() {
    }

    protected void end() {
    }

    public enum Options {
        CIRCLE,
        NONE
    }
}
