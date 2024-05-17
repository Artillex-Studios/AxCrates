package com.artillexstudios.axcrates.crates.openinganimation;

import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    public static ArrayList<Animation> animations = new ArrayList<>();
    protected final List<CrateReward> rewards;
    protected final Crate crate;
    protected final Location location;
    protected int frame = 0;
    protected int totalFrames;

    public Animation(int totalFrames, List<CrateReward> rewards, Crate crate, Location location) {
        this.totalFrames = totalFrames;
        this.rewards = rewards;
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

    protected void run() {
    }

    protected void end() {
    }

    public enum Options {
        CIRCLE
    }
}
